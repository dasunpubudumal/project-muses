package org.realtix.parameter;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Component
public final class ParameterStoreWrapper {

    private final SsmClient ssmClient;

    public ParameterStoreWrapper(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    public String getParameter(GetParameterRequest getParameterRequest) {
        return ssmClient.getParameter(getParameterRequest).parameter().value();
    }

}
