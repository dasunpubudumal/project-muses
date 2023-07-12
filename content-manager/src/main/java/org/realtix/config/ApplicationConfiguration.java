package org.realtix.config;

import org.realtix.processor.ContentProcessor;
import org.realtix.s3.S3ClientWrapper;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.transfer.BookRow;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class ApplicationConfiguration {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create("https://data-lake-realitix.s3.ap-south-1.amazonaws.com"))
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
    public ContentProcessor contentProcessor() {
        return new ContentProcessor();
    }

}
