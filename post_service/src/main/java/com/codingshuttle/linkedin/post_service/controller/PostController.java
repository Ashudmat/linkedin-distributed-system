package com.codingshuttle.linkedin.post_service.controller;

import com.codingshuttle.linkedin.post_service.dto.*;
import com.codingshuttle.linkedin.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(@ModelAttribute PostRequestDto postRequestDto) {
        return ResponseEntity.ok(postService.createPost(postRequestDto));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDto>> getFeed(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(postService.getFeed(page, size));
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.ok(postService.addComment(postId, requestDto));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequestDto updatePostRequestDto) {
        return ResponseEntity.ok(postService.updatePost(postId, updatePostRequestDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{postId}/repost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> repostPost(@PathVariable Long postId, @ModelAttribute RepostRequestDto repostRequestDto) {
        return ResponseEntity.ok(postService.repostPost(postId,repostRequestDto));
    }
}