package org.realtix.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.realtix.config.ExternalConfiguration;
import org.realtix.dynamodb.AbstractDynamoDbRepository;
import org.realtix.exception.ApplicationException;
import org.realtix.exception.AwsException;
import org.realtix.parameter.IParameterStore;
import org.realtix.repository.BookRepository;
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
    private final BookRepository<BookRow> repository;

    public ContentProcessor(S3FileTransferManager<BookRow> s3FileTransferManager,
                            ExternalConfiguration configuration,
                            BookRepository<BookRow> repository) {
        this.s3FileTransferManager = s3FileTransferManager;
        this.configuration = configuration;
        this.repository = repository;
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
            // persist in dynamodb
            List<BookRow> bookRows = bookRows(readAndProcessChunks);
            log.info("Final string length: {}", readAndProcessChunks.length());
            repository.saveBatch(bookRows, 5);
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
