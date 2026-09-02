package com.codingshuttle.linkedin.post_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReposted {
    private Long postId;
    private Long ownerUserId;
    private Long repostedByUserId;
}