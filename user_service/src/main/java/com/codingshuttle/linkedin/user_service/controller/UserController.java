package com.codingshuttle.linkedin.user_service.controller;

import com.codingshuttle.linkedin.user_service.dto.*;
import com.codingshuttle.linkedin.user_service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(authService.signup(signUpRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<UploadResponseDto> uploadProfileImage(@RequestHeader("X-User-Id") Long userId, @RequestParam("file") MultipartFile file) {
        String imageUrl = authService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(new UploadResponseDto(imageUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserResponseDto> updateProfile(@RequestHeader("X-User-Id") Long userId,  @RequestBody UpdateProfileRequestDto requestDto) {
        return ResponseEntity.ok(authService.updateProfile(userId, requestDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @DeleteMapping("/profile/photo")
    public ResponseEntity<Void> deleteProfilePhoto(@RequestHeader("X-User-Id") Long userId) {
        authService.deleteProfilePhoto(userId);
        return ResponseEntity.ok().build();
    }
}