package org.realtix.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.realtix.config.ExternalConfiguration;
import org.realtix.domain.BookRowEntity;
import org.realtix.exception.ApplicationException;
import org.realtix.exception.AwsException;
import org.realtix.repository.BookRepository;
import org.realtix.s3.S3FileTransferManager;
import org.realtix.transfer.BookRow;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ContentProcessor extends AbstractProcessor {

    private ProcessingContext context;
    private final S3FileTransferManager<BookRowEntity> s3FileTransferManager;
    private final ExternalConfiguration configuration;
    private final BookRepository<BookRowEntity> repository;
    private final ModelMapper modelMapper;

    public ContentProcessor(S3FileTransferManager<BookRowEntity> s3FileTransferManager,
                            ExternalConfiguration configuration,
                            BookRepository<BookRowEntity> repository) {
        this.s3FileTransferManager = s3FileTransferManager;
        this.configuration = configuration;
        this.repository = repository;
        this.modelMapper = new ModelMapper();
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
            List<BookRowEntity> bookRowEntities = bookRows
                    .stream()
                    .map(m -> modelMapper.map(m, BookRowEntity.class))
                    .collect(Collectors.toList());
            repository.saveBatch(bookRowEntities, 5);
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
