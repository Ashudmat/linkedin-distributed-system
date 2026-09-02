package com.codingshuttle.linkedin.post_service.event;

import lombok.*;

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