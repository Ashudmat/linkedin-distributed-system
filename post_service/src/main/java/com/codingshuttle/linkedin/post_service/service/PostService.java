package com.codingshuttle.linkedin.post_service.service;

import com.codingshuttle.linkedin.post_service.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto postRequestDto);

    PostResponseDto getPostById(Long postId);

    List<PostResponseDto> getAllPostsOfUser(Long userId);

    List<PostResponseDto> getFeed(int page, int size);

    CommentResponseDto addComment(Long postId, CommentRequestDto requestDto);

    List<CommentResponseDto> getComments(Long postId);

    PostResponseDto updatePost(Long postId, UpdatePostRequestDto updatePostRequestDto);

    void deletePost(Long postId);

    PostResponseDto repostPost(Long postId, RepostRequestDto requestDto);

    List<PostResponseDto> getUserPosts(Long userId);
}