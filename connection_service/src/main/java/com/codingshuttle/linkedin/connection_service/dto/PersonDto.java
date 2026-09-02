package com.codingshuttle.linkedin.connection_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private String connectionStatus;
}