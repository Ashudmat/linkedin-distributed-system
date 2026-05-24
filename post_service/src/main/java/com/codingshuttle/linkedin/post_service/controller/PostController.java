package com.codingshuttle.linkedin.post_service.controller;

import com.codingshuttle.linkedin.post_service.auth.AuthContextHolder;
import com.codingshuttle.linkedin.post_service.dto.CommentRequestDto;
import com.codingshuttle.linkedin.post_service.dto.CommentResponseDto;
import com.codingshuttle.linkedin.post_service.dto.PostRequestDto;
import com.codingshuttle.linkedin.post_service.dto.PostResponseDto;
import com.codingshuttle.linkedin.post_service.entity.Post;
import com.codingshuttle.linkedin.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(@ModelAttribute PostRequestDto postRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file){
        return ResponseEntity.ok(postService.createPost(postRequestDto, file));
    }

    @GetMapping("/id/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId){
        PostResponseDto postResponseDto = postService.getPostById(postId);
        return new ResponseEntity<>(postResponseDto, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/allposts")
    public ResponseEntity<List<PostResponseDto>> getAllPostsOfUser(@PathVariable Long userId){
        List<PostResponseDto> postResponseDtoList = postService.getAllPostsOfUser(userId);
        return new ResponseEntity<>(postResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDto>> getFeed(){
        return ResponseEntity.ok(postService.getFeed());
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto){
        return ResponseEntity.ok(postService.addComment(postId, requestDto));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long postId){
        return ResponseEntity.ok(postService.getComments(postId));
    }

}
