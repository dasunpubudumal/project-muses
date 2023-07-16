package org.realtix.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorEntity {

    private String id;
    private String name;
    private String role;

}
