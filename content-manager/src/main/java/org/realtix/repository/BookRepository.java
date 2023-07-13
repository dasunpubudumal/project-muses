package org.realtix.repository;

import lombok.extern.slf4j.Slf4j;
import org.realtix.dynamodb.AbstractDynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

@Slf4j
public class BookRepository<BookRow> extends AbstractDynamoDbRepository<BookRow> {

    public BookRepository(DynamoDbClient dynamoDb, String tableName, Class<BookRow> clazz) {
        super(dynamoDb, tableName, clazz);
    }

    @Override
    public void saveBatch(List<BookRow> items, int threadCount) {
        log.info("Saving {} items using {} threads.", items.size(), threadCount);
        super.saveBatch(items, threadCount);
    }
}
