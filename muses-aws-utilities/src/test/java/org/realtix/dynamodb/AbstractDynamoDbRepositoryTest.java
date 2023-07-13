package org.realtix.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SSM;

@Testcontainers
@Slf4j
@Tag("Integration")
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
    void scan() {
    }

    @Test
    void update() {
    }

    @Test
    @DisplayName("Given key, retrieve item from DynamoDB")
    void getItem() {
    }

    @Test
    void query() {
    }

    @Test
    void saveItem() throws IOException, InterruptedException {
        customRepository = new CustomRepository(
                dynamoDbClient,
                "my-data",
                CustomClass.class
        );
        localstack.execInContainer(
                "awslocal", "dynamodb", "create-table",
                "--table-name", "my-data", "--key-schema", "AttributeName=id,KeyType=HASH",
                "--attribute-definitions", "AttributeName=id,AttributeType=S",
                "--billing-mode", "PAY_PER_REQUEST",
                "--region", localstack.getRegion()
        );
        assertDoesNotThrow(() -> customRepository.saveItem(
                CustomClass.builder()
                        .id("123")
                        .age(29)
                        .name("Robert Oppenheimer")
                        .build()
        ));
    }

    private String createTable(DynamoDbClient ddb, String tableName, String key) {
        DynamoDbWaiter dbWaiter = ddb.waiter();
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
                .tableName(tableName)
                .build();

        String newTable ="";
        try {
            CreateTableResponse response = ddb.createTable(request);
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            // Wait until the Amazon DynamoDB table is created.
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            newTable = response.tableDescription().tableName();
            return newTable;

        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void setup() {
        localstack.start();
    }

    @AfterAll
    static void tearDown() {
        localstack.stop();
    }
}