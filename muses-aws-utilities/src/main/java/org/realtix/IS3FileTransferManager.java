package org.realtix;

import jdk.jshell.spi.ExecutionControl;
import org.realtix.exception.AwsException;

import java.util.List;

/**
 * File transfer manager for S3
 * @param <T> data type
 */
public interface IS3FileTransferManager<T> {

    /**
     * Get a list of objects of type T based on keys
     * @param keys S3 keys
     * @return list of objects of type T
     */
    default List<T> get(String bucket, String... keys) throws ExecutionControl.NotImplementedException, AwsException {
        throw new ExecutionControl.NotImplementedException(
                "Not Implemented"
        );
    }

    /**
     * Get an object of type T
     * @param key key of the object
     * @return returned object
     */
    default T get(String key, String bucket) throws ExecutionControl.NotImplementedException, AwsException {
        throw new ExecutionControl.NotImplementedException(
                "Not Implemented"
        );
    }

    /**
     * Insert data for key
     * @param data data
     * @param key key of the object
     */
    default void put(String bucket, String key, T data) throws ExecutionControl.NotImplementedException, AwsException {
        throw new ExecutionControl.NotImplementedException(
                "Not Implemented"
        );
    }

    /**
     * Replace the file represented by key with data
     * @param data data
     * @param key key
     */
    default void replace(String key, T data) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException(
                "Not Implemented"
        );
    }

    /**
     * Remove an object denoted by key
     * @param key key of the object
     */
    default void remove(String key) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException(
                "Not Implemented"
        );
    }

}
