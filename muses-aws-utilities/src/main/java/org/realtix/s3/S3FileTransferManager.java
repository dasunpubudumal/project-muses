package org.realtix.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.realtix.ObjectMapperSingleton;
import org.realtix.exception.AwsException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public final class S3FileTransferManager<T extends ConversionBound> implements IS3FileTransferManager<T> {

    private final Class<T> outputClass;
    private final S3ClientWrapper s3ClientWrapper;

    public S3FileTransferManager(S3ClientWrapper s3ClientWrapper, Class<T> outputClass) {
        this.s3ClientWrapper = s3ClientWrapper;
        this.outputClass = outputClass;
    }

    @Override
    public T get(String key, String bucket) throws AwsException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .key(key)
                .bucket(bucket)
                .build();
        String responseString = s3ClientWrapper.getObjectString(getObjectRequest);
        try {
            return convert(
                    responseString,
                    outputClass
            );
        } catch (JsonProcessingException e) {
            throw new AwsException(e.getMessage());
        }
    }

    @Override
    public List<T> get(String bucket, String... keys) throws AwsException {
        final List<T> output = new ArrayList<>();
        for (String key : keys) {
            output.add(get(key, bucket));
        }
        return output;
    }

    @Override
    public void put(String bucket, String key, T data) throws AwsException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3ClientWrapper.insertObject(
                    putObjectRequest,
                    ObjectMapperSingleton.INSTANCE.mapper().writeValueAsString(data)
            );
        } catch (JsonProcessingException e) {
            throw new AwsException(e.getMessage());
        }
    }

    @Override
    public void remove(String key, String bucket) throws AwsException {
        try {
            Delete del = Delete.builder()
                    .objects(Collections.singleton(
                            ObjectIdentifier.builder().key(key).build()
                    ))
                    .build();
            DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(del)
                    .build();
            s3ClientWrapper.remove(multiObjectDeleteRequest);
        } catch (Exception e) {
            throw new AwsException(e.getMessage());
        }
    }

    private T convert(String responseString, Class<T> outputClass) throws JsonProcessingException {
        return ObjectMapperSingleton.INSTANCE
                .mapper()
                .readValue(responseString, outputClass);
    }

}
