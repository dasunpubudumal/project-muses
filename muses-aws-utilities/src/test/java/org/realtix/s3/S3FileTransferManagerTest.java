package org.realtix.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.realtix.ObjectMapperSingleton;
import org.realtix.exception.AwsException;
import org.realtix.s3.ConversionBound;
import org.realtix.s3.S3ClientWrapper;
import org.realtix.s3.S3FileTransferManager;

import java.net.URI;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTests")
class S3FileTransferManagerTest {

    S3ClientWrapper s3ClientWrapper = mock(
            S3ClientWrapper.class
    );
    S3FileTransferManager<Person> transferManager;
    String returnValue = "{\"name\":\"Dasun\"}";

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Person extends ConversionBound {
        private String name;

        @Override
        public int hashCode() {
            return (new Random()).nextInt();
        }
    }

    @BeforeEach
    void setUp() {
        transferManager = new S3FileTransferManager<Person>(
                s3ClientWrapper,
                Person.class
        );
        when(s3ClientWrapper.getObjectString(
                any()
        )).thenReturn(returnValue);
        doNothing()
                .when(s3ClientWrapper)
                .insertObject(any(), any());
    }

    @Test
    @DisplayName("Test conversion of the string")
    void testStringConversion() throws JsonProcessingException {
        Person person = ObjectMapperSingleton.INSTANCE.mapper()
                .readValue(returnValue, Person.class);
        assertEquals(
                "Dasun",
                person.getName()
        );
    }

    @Test
    @DisplayName("Test conversion of the string")
    void testStringConversionGeneric() throws JsonProcessingException {
        Person person = ObjectMapperSingleton.INSTANCE.mapper()
                .readValue(returnValue, new TypeReference<Person>() {
                });
        assertEquals(
                "Dasun",
                person.getName()
        );
    }

    @Test
    @DisplayName("Test retrieving from S3")
    void get() throws AwsException {
        Person person = transferManager.get("", "");
        assertEquals(
                "Dasun",
                person.getName()
        );
    }

    @Test
    @DisplayName("Given set of keys and buckets, fetch list of objects")
    void getListOfObjects() throws AwsException {
        List<Person> people = transferManager.get("", "", "", "");
        assertEquals(3, people.size());
    }

    @Test
    @DisplayName("Given a string object, store it as a file")
    void put() throws AwsException {
        assertDoesNotThrow(() ->
                transferManager.put("", "", Person.builder().name("Dasun").build()));

    }

    @Test
    void testResolveUriNull() {
        URI uri = URI.create("http://data-lake.realitix.org.127.0.0.1:64937");
        assertNull(uri.getHost());
    }

    @Test
    void testResolveUriNotNull() {
        URI uri = URI.create("http://127.0.0.1:64937");
        assertNotNull(uri.getHost());
    }

}