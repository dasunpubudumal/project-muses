package org.realtix.processor;

import org.realtix.exception.ApplicationException;

public abstract class AbstractProcessor {

    public void processToCompletion() {
        validateProcessorContext();
        process();
    }

    public abstract void setContext(ProcessingContext processingContext);

    protected abstract void process();

    protected abstract void validateProcessorContext() throws ApplicationException;

}
