package org.realtix.parameter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SSM;

@Testcontainers
@Slf4j
@Tag("Integration")
class ParameterStoreIntegrationTest {

    ParameterStore parameterStore;
    private static final String PARAMETER_NAME = "key";
    private static final String PARAMETER_VALUE = "value";

    @Container
    public static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.0.0"))
            .withServices(SSM);

    SsmClient ssmClient = SsmClient.builder()
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                    )
            )
            .endpointOverride(localstack.getEndpointOverride(SSM))
            .region(Region.of(localstack.getRegion()))
            .build();

    @BeforeEach
    void setUpEach() {
        parameterStore = new ParameterStore(
                new ParameterStoreWrapper(ssmClient)
        );
    }

    @Test
    @DisplayName("Given parameter key, retrieve the value")
    void testParameterGet() {
        putParameter();
        GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder()
                .name(PARAMETER_NAME)
                .build());
        String parameterValue = response.parameter().value();
        assertEquals(PARAMETER_VALUE, parameterValue);
    }

    private void putParameter() {
        ssmClient.putParameter(
                PutParameterRequest.builder()
                        .name(PARAMETER_NAME)
                        .type(ParameterType.STRING)
                        .value(PARAMETER_VALUE)
                        .build()
        );
    }

    @BeforeAll
    static void setUp() {
        localstack.start();
    }

    @AfterAll
    static void tearDown() {
        localstack.stop();
    }
}