package org.realtix.s3;

import org.realtix.exception.AwsException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Collections;

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

    public void remove(DeleteObjectsRequest multiObjectDeleteRequest) throws AwsException {
        try {
            s3Client.deleteObjects(multiObjectDeleteRequest);
        } catch (Exception e) {
            throw new AwsException(e.getMessage());
        }
    }
}
