package org.realtix.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.realtix.config.ExternalConfiguration;
import org.realtix.exception.ApplicationException;
import org.realtix.exception.AwsException;
import org.realtix.parameter.IParameterStore;
import org.realtix.s3.S3ClientWrapper;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.transfer.BookRow;
import org.realtix.util.Constants;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ContentProcessor extends AbstractProcessor {

    private ProcessingContext context;
    private final S3FileTransferManager<BookRow> s3FileTransferManager;
    private final ExternalConfiguration configuration;

    public ContentProcessor(S3FileTransferManager<BookRow> s3FileTransferManager,
                            ExternalConfiguration configuration) {
        this.s3FileTransferManager = s3FileTransferManager;
        this.configuration = configuration;
    }

    @Override
    public void setContext(ProcessingContext processingContext) {
        this.context = processingContext;
    }

    @Override
    public void process() {
        log.info("Processing Content.");
        try {
            String readAndProcessChunks = s3FileTransferManager.readAndProcessChunks(
                    configuration.getContentFileName(),
                    configuration.getBucketName()
            );
            List<BookRow> bookRows = bookRows(readAndProcessChunks);
            // persist in dynamodb
            log.info("Final string length: {}", readAndProcessChunks.length());
        } catch (AwsException | JsonProcessingException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    private List<BookRow> bookRows(String bookRowsString) throws JsonProcessingException {
        return BookRow.ofList(bookRowsString);
    }

    @Override
    protected void validateProcessorContext() {
        if (StringUtils.isEmpty(context.getCommand())) {
            throw new ApplicationException("No command provided.");
        }
    }



}
