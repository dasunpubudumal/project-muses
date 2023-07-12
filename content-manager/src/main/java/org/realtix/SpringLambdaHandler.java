package org.realtix;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;
import org.realtix.config.ApplicationConfiguration;
import org.realtix.processor.AbstractProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.realtix.util.ProcessorFactory.getProcessor;

@Slf4j
public class SpringLambdaHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        try {
            log.info("Starting lambda container...");
            AnnotationConfigApplicationContext applicationContext
                    = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            getProcessor(inputStream, applicationContext)
                    .ifPresentOrElse(AbstractProcessor::processToCompletion, () -> log.error("Invalid processor."));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
