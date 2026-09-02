package com.codingshuttle.linkedin.user_service.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {

    Long id;
    String name;
    String email;
    String profileImageUrl;

    String headline;
    String about;
    String location;
    String skills;
}