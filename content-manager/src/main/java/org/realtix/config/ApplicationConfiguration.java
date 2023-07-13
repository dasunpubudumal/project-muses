package org.realtix.config;

import org.realtix.parameter.IParameterStore;
import org.realtix.parameter.ParameterStore;
import org.realtix.parameter.ParameterStoreWrapper;
import org.realtix.processor.ContentProcessor;
import org.realtix.s3.S3ClientWrapper;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.transfer.BookRow;
import org.realtix.util.Constants;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.net.URI;

public class ApplicationConfiguration {

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
    public S3FileTransferManager<BookRow> s3ClientWrapper(S3ClientWrapper s3ClientWrapper) {
        return new S3FileTransferManager<>(s3ClientWrapper, BookRow.class);
    }

    @Bean
    public ContentProcessor contentProcessor(
            S3FileTransferManager<BookRow> s3FileTransferManager,
            IParameterStore parameterStore,
            ExternalConfiguration externalConfiguration) {
        return new ContentProcessor(s3FileTransferManager, externalConfiguration);
    }

    @Bean
    public ExternalConfiguration configuration(IParameterStore parameterStore) {
        return new ExternalConfiguration(parameterStore);
    }

}
