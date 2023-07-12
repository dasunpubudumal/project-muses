package org.realtix.s3;

import org.realtix.exception.AwsException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
public final class S3ClientWrapper {

    private final S3Client s3Client;

    public S3ClientWrapper(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * @param bucketName S3 bucket name
     * @param key S3 object key
     * @param chunkSize size of each chunk
     * @param executable e.g. (str) -> str.length()
     * @throws AwsException thrown exception
     */
    public void processObjectByChunks(String bucketName,
                                      String key,
                                      int chunkSize,
                                      Consumer<String> executable)
            throws AwsException {

        HeadObjectResponse headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
        );

        long currentPosition = 0;
        long remainingBytes = headObjectResponse.contentLength();

        while (remainingBytes > 0) {
            // Calculate the chunk size for the current iteration
            int currentChunkSize = (int) Math.min(chunkSize, remainingBytes);

            // Create a range header specifying the current position and chunk size
            String rangeHeaderValue = String.format(
                    "bytes=%d-%d",
                    currentPosition,
                    currentPosition + currentChunkSize - 1
            );

            // Build the GetObjectRequest with the range header
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .range(rangeHeaderValue)
                    .build();

            try (InputStream objectContent = s3Client.getObject(getObjectRequest)) {
                executable.accept(
                     new String(objectContent.readAllBytes(), StandardCharsets.UTF_8)
                );
                // Update the current position and remaining bytes
                currentPosition += currentChunkSize;
                remainingBytes -= currentChunkSize;
            } catch (IOException e) {
                throw new AwsException(e.getMessage());
            }
        }
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
