package org.realtix.repository;

import lombok.extern.slf4j.Slf4j;
import org.realtix.dynamodb.AbstractDynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

@Slf4j
public class BookRepository<BookRowEntity> extends AbstractDynamoDbRepository<BookRowEntity> {

    public BookRepository(DynamoDbClient dynamoDb, String tableName, Class<BookRowEntity> clazz) {
        super(dynamoDb, tableName, clazz);
    }

    @Override
    public void saveBatch(List<BookRowEntity> items, int threadCount) {
        log.info("Saving {} items using {} threads.", items.size(), threadCount);
        super.saveBatch(items, threadCount);
    }
}
