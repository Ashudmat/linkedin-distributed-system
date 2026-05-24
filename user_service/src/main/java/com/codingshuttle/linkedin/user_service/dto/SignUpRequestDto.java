package com.codingshuttle.linkedin.user_service.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpRequestDto {
    String name;
    String email;
    String password;
}
