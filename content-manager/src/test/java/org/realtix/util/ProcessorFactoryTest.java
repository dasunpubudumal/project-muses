package org.realtix.util;

import org.junit.jupiter.api.*;
import org.realtix.config.ExternalConfiguration;
import org.realtix.dynamodb.AbstractDynamoDbRepository;
import org.realtix.exception.AwsException;
import org.realtix.processor.AbstractProcessor;
import org.realtix.processor.ContentProcessor;
import org.realtix.repository.BookRepository;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.transfer.BookRow;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTests")
class ProcessorFactoryTest {

    String inputString = "{\n" +
            "  \"command\": \"process-content\"\n" +
            "}";

    AnnotationConfigApplicationContext annotationConfigApplicationContext = mock(
            AnnotationConfigApplicationContext.class
    );

    S3FileTransferManager<BookRow> s3FileTransferManager = mock(
            S3FileTransferManager.class
    );

    ExternalConfiguration externalConfiguration = mock(
            ExternalConfiguration.class
    );

    BookRepository<BookRow> repository = mock(
            BookRepository.class
    );

    @BeforeEach
    void setUp() throws AwsException {
        when(s3FileTransferManager.readAndProcessChunks(
                any(),
                any()
        )).thenReturn("[{\"isbn\": \"123121\"}]");
        doNothing().when(repository).saveBatch(any(), anyInt());
        when(annotationConfigApplicationContext.getBean(ContentProcessor.class))
                .thenReturn(new ContentProcessor(s3FileTransferManager, externalConfiguration, repository));
    }

    @Test
    @DisplayName("Given processor, check context is not null")
    void checkContextExists() {
        InputStream byteArrayInputStream = new ByteArrayInputStream(
                inputString.getBytes(StandardCharsets.UTF_8)
        );
        Optional<AbstractProcessor> processor = ProcessorFactory
                .getProcessor(byteArrayInputStream, annotationConfigApplicationContext);
        assertDoesNotThrow(
                () -> processor.get().processToCompletion()
        );
    }

}