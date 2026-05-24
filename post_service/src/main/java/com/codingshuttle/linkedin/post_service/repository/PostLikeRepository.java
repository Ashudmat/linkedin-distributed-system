package com.codingshuttle.linkedin.post_service.repository;

import com.codingshuttle.linkedin.post_service.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByuserIdAndPostId(Long userId, long postId);

    void deleteByUserIdAndPostId(Long userId, long postId);
}
