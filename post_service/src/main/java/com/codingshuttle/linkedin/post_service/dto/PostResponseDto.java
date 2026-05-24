package com.codingshuttle.linkedin.post_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private String mediaUrl;
    private String authorName;
    private boolean ownPost;

}
