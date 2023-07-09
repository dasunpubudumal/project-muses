package org.realtix;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public final class S3ClientWrapper {

    private final S3Client s3Client;

    public S3ClientWrapper(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String getObjectString(GetObjectRequest getObjectRequest) {
        return s3Client
                .getObject(getObjectRequest, ResponseTransformer.toBytes())
                .asUtf8String();
    }

    public void insertObject(PutObjectRequest putObjectRequest, String stringData) {
        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromString(stringData)
        );
    }
}
