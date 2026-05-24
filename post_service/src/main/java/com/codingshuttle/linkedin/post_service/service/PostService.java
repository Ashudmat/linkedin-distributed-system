package com.codingshuttle.linkedin.post_service.service;

import com.codingshuttle.linkedin.post_service.dto.CommentRequestDto;
import com.codingshuttle.linkedin.post_service.dto.CommentResponseDto;
import com.codingshuttle.linkedin.post_service.dto.PostRequestDto;
import com.codingshuttle.linkedin.post_service.dto.PostResponseDto;
import com.codingshuttle.linkedin.post_service.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile file);

    PostResponseDto getPostById(Long postId);

    List<PostResponseDto> getAllPostsOfUser(Long userId);

    List<PostResponseDto> getFeed();

    CommentResponseDto addComment(Long postId, CommentRequestDto requestDto);

    List<CommentResponseDto> getComments(Long postId);
}
