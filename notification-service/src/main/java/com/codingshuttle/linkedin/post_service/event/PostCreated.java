package com.codingshuttle.linkedin.post_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreated {
    private Long postId;
    private Long authorId; // whose post ?
    private Long receiverUserId; // whom to notify ?
    private String content;
    private LocalDateTime createdAt;
}