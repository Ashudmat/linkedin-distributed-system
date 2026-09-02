package com.codingshuttle.linkedin.post_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private String authorProfileImageUrl;
}