package org.realtix.config;

import org.realtix.parameter.IParameterStore;
import org.realtix.parameter.ParameterStore;
import org.realtix.parameter.ParameterStoreWrapper;
import org.realtix.processor.ContentProcessor;
import org.realtix.repository.BookRepository;
import org.realtix.s3.S3ClientWrapper;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.domain.BookRowEntity;
import org.realtix.util.Constants;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.net.URI;

public class ApplicationConfiguration {

    @Bean
    public BookRepository<BookRowEntity> dbRepository(DynamoDbClient dynamoDbClient) {
        return new BookRepository<>(
                dynamoDbClient,
                Constants.DynamoDB.BOOKS_TABLE_NAME,
                BookRowEntity.class
        );
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder().build();
    }

    @Bean
    public ParameterStoreWrapper parameterStoreWrapper(SsmClient ssmClient) {
        return new ParameterStoreWrapper(ssmClient);
    }

    @Bean
    public IParameterStore parameterStore(ParameterStoreWrapper parameterStoreWrapper) {
        return new ParameterStore(parameterStoreWrapper);
    }

    @Bean
    public S3Client s3Client(IParameterStore parameterStore, ExternalConfiguration externalConfiguration) {
        return S3Client.builder()
                .endpointOverride(URI.create(
                        parameterStore.getParameter(
                                externalConfiguration.getPathUrlS3()
                        )
                ))
                .build();
    }

    @Bean
    public S3ClientWrapper s3ClientWrapper(S3Client s3Client) {
        return new S3ClientWrapper(s3Client);
    }

    @Bean
    public S3FileTransferManager<BookRowEntity> s3ClientWrapper(S3ClientWrapper s3ClientWrapper) {
        return new S3FileTransferManager<>(s3ClientWrapper, BookRowEntity.class);
    }

    @Bean
    public ContentProcessor contentProcessor(
            S3FileTransferManager<BookRowEntity> s3FileTransferManager,
            ExternalConfiguration externalConfiguration,
            BookRepository<BookRowEntity> repository) {
        return new ContentProcessor(s3FileTransferManager, externalConfiguration, repository);
    }

    @Bean
    public ExternalConfiguration configuration(IParameterStore parameterStore) {
        return new ExternalConfiguration(parameterStore);
    }

}
