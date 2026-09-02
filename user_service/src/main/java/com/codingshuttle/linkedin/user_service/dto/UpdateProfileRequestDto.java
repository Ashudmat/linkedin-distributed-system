package com.codingshuttle.linkedin.user_service.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    private String name;
    private String headline;
    private String about;
    private String location;
    private String skills;
}