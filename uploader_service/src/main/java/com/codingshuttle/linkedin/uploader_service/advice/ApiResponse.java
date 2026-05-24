package com.codingshuttle.linkedin.uploader_service.advice;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    ApiError error;
    T data;
    LocalDateTime timestamp;

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
}