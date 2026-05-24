package com.codingshuttle.linkedin.post_service.dto;

import com.codingshuttle.linkedin.post_service.enums.MediaType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRequestDto {
    private String content;
    private String mediaUrl;
    private MediaType mediaType;
}
