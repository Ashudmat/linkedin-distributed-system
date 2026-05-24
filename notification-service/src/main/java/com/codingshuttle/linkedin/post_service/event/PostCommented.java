package com.codingshuttle.linkedin.post_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommented {

    private Long postId;
    private Long commenterUserId;
    private Long ownerUserId;
    private String comment;
}