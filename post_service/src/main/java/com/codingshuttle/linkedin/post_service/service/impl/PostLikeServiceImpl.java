package com.codingshuttle.linkedin.post_service.service.impl;

import com.codingshuttle.linkedin.post_service.auth.AuthContextHolder;
import com.codingshuttle.linkedin.post_service.entity.Post;
import com.codingshuttle.linkedin.post_service.entity.PostLike;
import com.codingshuttle.linkedin.post_service.event.PostLiked;
import com.codingshuttle.linkedin.post_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.post_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.post_service.repository.PostLikeRepository;
import com.codingshuttle.linkedin.post_service.repository.PostRepository;
import com.codingshuttle.linkedin.post_service.service.PostLikeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final KafkaTemplate<String, PostLiked> postLikeKafkaTemplate;

    @Override
    @Transactional
    public void likePost(Long postId) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        Post post = getPostOrThrow(postId);
        validatePostNotAlreadyLiked(currentUserId, postId);
        savePostLike(currentUserId, postId);
        publishPostLikedEvent(post, currentUserId);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        getPostOrThrow(postId);
        validatePostAlreadyLiked(currentUserId, postId);
        postLikeRepository.deleteByUserIdAndPostId(currentUserId, postId);
    }

    // =========================
    // PRIVATE METHODS
    // =========================

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private void validatePostNotAlreadyLiked(Long userId, Long postId) {
        boolean alreadyLiked = postLikeRepository.existsByuserIdAndPostId(userId, postId);
        if (alreadyLiked) {throw new BadRequestException("You have already liked this post");
        }
    }

    private void validatePostAlreadyLiked(Long userId, Long postId) {
        boolean alreadyLiked = postLikeRepository.existsByuserIdAndPostId(userId, postId);
        if (!alreadyLiked) {throw new BadRequestException("You have not liked this post yet");
        }
    }

    private void savePostLike(Long userId, Long postId) {
        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        postLikeRepository.save(postLike);
    }

    private void publishPostLikedEvent(Post post, Long likedByUserId) {
        if(post.getUserId().equals(likedByUserId)){
            return;
        }
        PostLiked postLiked = PostLiked.builder()
                .postId(post.getId())
                .ownerUserId(post.getUserId())
                .likedByUserId(likedByUserId)
                .build();

        postLikeKafkaTemplate.send("post_liked_topic", post.getUserId().toString(), postLiked);
    }
}