package org.realtix.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@Testcontainers
@Slf4j
@Tag("Integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AbstractDynamoDbRepositoryTest {

    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @DynamoDbBean
    public static class CustomClass {
        private String id;
        private String name;
        private Integer age;

        @DynamoDbPartitionKey
        @DynamoDbAttribute(value = "id")
        public String getId() {
            return id;
        }

        @DynamoDbAttribute(value = "name")
        public String getName() {
            return name;
        }

        @DynamoDbAttribute(value = "age")
        public Integer getAge() {
            return age;
        }
    }

    public static class CustomRepository extends AbstractDynamoDbRepository<CustomClass> {
        protected CustomRepository(DynamoDbClient dynamoDb, String tableName, Class<CustomClass> clazz) {
            super(dynamoDb, tableName, clazz);
        }
    }

    static CustomRepository customRepository;

    @Container
    public static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.0.0"))
            .withServices(DYNAMODB);

    DynamoDbClient dynamoDbClient =  DynamoDbClient.builder()
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                    )
            )
            .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
            .region(Region.of(localstack.getRegion()))
            .build();


    @Test
    @DisplayName("Given object, save it")
    @Order(1)
    void saveItem() {
        assertDoesNotThrow(() -> customRepository.saveItem(
                CustomClass.builder()
                        .id("123")
                        .age(29)
                        .name("Robert Oppenheimer")
                        .build()
        ));
    }

    @Test
    @Order(2)
    @DisplayName("Given key, retrieve item from DynamoDB")
    void getItem() {
        CustomClass item = customRepository.getItem(Key.builder().partitionValue("123").build());
        assertNotNull(item);
        assertEquals(
                29,
                item.getAge()
        );
    }

    @Test
    @Order(3)
    @DisplayName("Given key, update the corresponding record")
    void update() {
        assertDoesNotThrow(() -> customRepository.update(
                CustomClass.builder()
                        .id("123")
                        .age(100)
                        .name("Robert Oppenheimer")
                        .build()
        ));
    }

    @Test
    @Order(4)
    @DisplayName("Given key, remove the corresponding item")
    void removeItem() {
        assertDoesNotThrow(() ->
                customRepository.removeItem(Key.builder().partitionValue("123").build()));
    }

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        localstack.start();
        localstack.execInContainer(
                "awslocal", "dynamodb", "create-table",
                "--table-name", "my-data", "--key-schema", "AttributeName=id,KeyType=HASH",
                "--attribute-definitions", "AttributeName=id,AttributeType=S",
                "--billing-mode", "PAY_PER_REQUEST",
                "--region", localstack.getRegion()
        );
    }

    @BeforeEach
    void setUp() {
        customRepository = new CustomRepository(
                dynamoDbClient,
                "my-data",
                CustomClass.class
        );
    }

    @AfterAll
    static void tearDown() {
        localstack.stop();
    }
}