package org.realtix.config;

import org.realtix.exception.ApplicationException;
import org.realtix.parameter.IParameterStore;
import org.realtix.util.Constants;
import software.amazon.awssdk.utils.StringUtils;

public class ExternalConfiguration {

    private final IParameterStore parameterStore;

    public ExternalConfiguration(IParameterStore parameterStore) {
        this.parameterStore = parameterStore;
    }

    public String getBucketName() {
        String bucketName = parameterStore.getParameter(Constants.Configuration.BUCKET_NAME);
        if (StringUtils.isEmpty(bucketName)) {
            throw new ApplicationException("Bucket name not found!");
        }
        return bucketName;
    }

    public String getPathUrlS3() {
        String pathUrl = parameterStore.getParameter(Constants.Configuration.PARAMETER_STORE_PATH_CONTENT);
        if (StringUtils.isEmpty(pathUrl)) {
            throw new ApplicationException("Path URL not found!");
        }
        return pathUrl;
    }

    public String getContentFileName() {
        String fileName = parameterStore.getParameter(Constants.Configuration.FILE_NAME);
        if (StringUtils.isEmpty(fileName)) {
            throw new ApplicationException("File name not found!");
        }
        return fileName;
    }

}
