package org.realtix;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.realtix.exception.AwsException;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@Slf4j
@Tag("Integration")
public class S3FileTransferManagerIntegrationTest {

    private static final String DATA_LAKE_NAME = "realtixdatalake";

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
    S3FileTransferManager<Person> transferManager;

    @Container
    public static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.0.0"))
            .withServices(S3);

    S3Client s3 = S3Client
            .builder()
            .endpointOverride(localstack.getEndpoint())
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                    )
            )
            .region(Region.of(localstack.getRegion()))
            .build();

    @BeforeAll
    static void beforeAll() {
        localstack.start();
    }

    @BeforeEach
    void setUp() {
        createBucket();
        transferManager = new S3FileTransferManager<>(
                new S3ClientWrapper(s3), Person.class
        );
    }

    @Test
    @DisplayName("Given a string, save and verify a file is saved.")
    void testPut() throws AwsException {
        transferManager.put(
                DATA_LAKE_NAME,
                "person.txt",
                Person.builder().name("Dasun Pubudumal").build()
        );
        String string = s3.getObject(
                GetObjectRequest.builder()
                        .bucket(DATA_LAKE_NAME)
                        .key("person.txt").build(),
                ResponseTransformer.toBytes()
        ).asUtf8String();
        assertNotNull(
                string
        );
    }

    private void createBucket() {
        S3Waiter s3Waiter = s3.waiter();
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(DATA_LAKE_NAME)
                .build();

        s3.createBucket(bucketRequest);
        HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(DATA_LAKE_NAME)
                .build();
        // Wait until the bucket is created and print out the response.
        WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
        waiterResponse.matched().response().ifPresent(v -> log.info("{}", v));
    }

    @Test
    @DisplayName("Given a bucket and a file key, check whether the file is retrieved")
    void testGet() throws AwsException {
        createBucketAndInsertJsonFile();
        Person person = transferManager.get("person.json", DATA_LAKE_NAME);
        assertNotNull(person);
        assertEquals("Dasun Pubudumal", person.getName());
    }

    private void createBucketAndInsertJsonFile() {
        createBucket();
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(DATA_LAKE_NAME)
                .key("person.json")
                .build();
        s3.putObject(putOb, RequestBody.fromFile(new File(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("sample-json.json")).getFile()
        )));
    }

    @AfterAll
    static void tearDown() {
        localstack.stop();
    }
}
