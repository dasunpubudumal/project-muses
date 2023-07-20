package org.realtix.domain;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Builder
public class AuthorEntity {

    private String id;
    private String name;
    private String role;

}
