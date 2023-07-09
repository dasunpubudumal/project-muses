package org.realtix.parameter;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Component
public final class ParameterStore implements IParameterStore {

    private final ParameterStoreWrapper parameterStoreWrapper;

    public ParameterStore(ParameterStoreWrapper parameterStoreWrapper) {
        this.parameterStoreWrapper = parameterStoreWrapper;
    }

    @Override
    public String getParameter(String key) {
        GetParameterRequest getParameterRequest = GetParameterRequest.builder()
                .name(key)
                .build();
        return parameterStoreWrapper.getParameter(getParameterRequest);
    }

}
