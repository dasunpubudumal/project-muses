package org.realtix.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractDynamoDbRepository<T> {

    private final DynamoDbTable<T> table;
    private final Class<T> clazz;

    protected AbstractDynamoDbRepository(DynamoDbTable<T> table, Class<T> clazz) {
        this.table = table;
        this.clazz = clazz;
    }

    protected AbstractDynamoDbRepository(DynamoDbClient dynamoDb, String tableName, Class<T> clazz) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient
                = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDb).build();
        this.table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(clazz));
        this.clazz = clazz;
    }

    protected List<T> scan() {
        return (List)this.table.scan().stream().flatMap((page) -> {
            return page.items().stream();
        }).collect(Collectors.toList());
    }

    protected void update(T item) {
        UpdateItemEnhancedRequest<T> updateItemEnhancedRequest
                = UpdateItemEnhancedRequest.builder(this.clazz).item(item).ignoreNulls(true).build();
        this.table.updateItem(updateItemEnhancedRequest);
    }

    protected T getItem(Key key) {
        return this.table.getItem(key);
    }

    protected List<T> query(QueryEnhancedRequest querySpec, String indexName) {
        return indexName == null ? this.queryTable(querySpec) : this.queryIndex(querySpec, indexName);
    }

    private List<T> queryIndex(QueryEnhancedRequest querySpec, String indexName) {
        DynamoDbIndex<T> index = this.table.index(indexName);
        if (querySpec.filterExpression() != null) {
            return this.queryIndexWithFilterExpression(querySpec, index);
        } else {
            List<T> dataList = new ArrayList();
            Iterator var4 = index.query((r) -> {
                r.queryConditional(querySpec.queryConditional());
            }).iterator();

            while(var4.hasNext()) {
                Page<T> next = (Page)var4.next();
                dataList.addAll(next.items());
            }

            return dataList;
        }
    }

    private List<T> queryIndexWithFilterExpression(QueryEnhancedRequest querySpec, DynamoDbIndex<T> index) {
        List<T> dataList = new ArrayList();
        Iterator var4 = index.query((r) -> {
            r.queryConditional(querySpec.queryConditional()).filterExpression(querySpec.filterExpression());
        }).iterator();

        while(var4.hasNext()) {
            Page<T> next = (Page)var4.next();
            dataList.addAll(next.items());
        }

        return dataList;
    }

    private List<T> queryTable(QueryEnhancedRequest querySpec) {
        List<T> dataList = new ArrayList();
        Iterator var3 = this.table.query((r) -> {
            r.queryConditional(querySpec.queryConditional());
        }).iterator();

        while(var3.hasNext()) {
            Page<T> next = (Page)var3.next();
            dataList.addAll(next.items());
        }

        return dataList;
    }

    protected void saveItem(T item) {
        this.table.putItem(item);
    }

}
