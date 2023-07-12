package org.realtix.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.realtix.ObjectMapperSingleton;
import org.realtix.exception.ApplicationException;
import org.realtix.processor.AbstractProcessor;
import org.realtix.processor.ContentProcessor;
import org.realtix.processor.ProcessingContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@UtilityClass
@Slf4j
public class ProcessorFactory {

    public static Optional<AbstractProcessor> getProcessor(InputStream inputStream,
                                                           AnnotationConfigApplicationContext context)
            throws ApplicationException {
        try {
            String command = getCommand(inputStream);
            if ("process-content".equals(command)) {
                ProcessingContext build = ProcessingContext.builder()
                        .command(command)
                        .build();
                ContentProcessor bean = context.getBean(ContentProcessor.class);
                bean.setContext(build);
                return Optional.of(
                        bean
                );
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    private String getCommand(InputStream inputStream) throws IOException {
        Map map = ObjectMapperSingleton.INSTANCE.mapper().readValue(
                inputStream,
                Map.class
        );
        return (String) map.get("command");
    }

}
