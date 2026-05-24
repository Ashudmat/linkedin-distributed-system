package com.codingshuttle.linkedin.post_service.repository;

import com.codingshuttle.linkedin.post_service.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}