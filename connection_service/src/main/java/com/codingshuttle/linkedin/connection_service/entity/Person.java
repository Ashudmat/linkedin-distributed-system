package com.codingshuttle.linkedin.connection_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("User")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    private Long id;
    private Long userId;
    private String name;
    private String email;
}