package com.codingshuttle.linkedin.post_service.controller;



import com.codingshuttle.linkedin.post_service.advice.ApiResponse;
import com.codingshuttle.linkedin.post_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable long postId){
        postLikeService.likePost(postId);
        return ResponseEntity.ok(new ApiResponse<>("Post Liked Successfully!"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable long postId){
        postLikeService.unlikePost(postId);
        return ResponseEntity.ok(new ApiResponse<>("Post Unliked Successfully!"));
    }
}
