package com.codingshuttle.linkedin.post_service.repository;

import com.codingshuttle.linkedin.post_service.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByUserId(Long userId);

    Page<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
