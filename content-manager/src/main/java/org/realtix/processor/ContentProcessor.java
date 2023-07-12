package org.realtix.processor;

import lombok.extern.slf4j.Slf4j;
import org.realtix.exception.ApplicationException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.utils.StringUtils;

@Component
@Slf4j
public class ContentProcessor extends AbstractProcessor {

    private ProcessingContext context;

    @Override
    public void setContext(ProcessingContext processingContext) {
        this.context = processingContext;
    }

    @Override
    public void process() {
        log.info("Processing Content!");
    }

    @Override
    protected void validateProcessorContext() {
        if (StringUtils.isEmpty(context.getCommand())) {
            throw new ApplicationException("No command provided.");
        }
    }



}
