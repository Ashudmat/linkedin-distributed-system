package com.codingshuttle.linkedin.user_service.controller;

import com.codingshuttle.linkedin.user_service.dto.LoginRequestDto;
import com.codingshuttle.linkedin.user_service.dto.LoginResponseDto;
import com.codingshuttle.linkedin.user_service.dto.SignUpRequestDto;
import com.codingshuttle.linkedin.user_service.dto.UserResponseDto;
import com.codingshuttle.linkedin.user_service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto){
        return ResponseEntity.ok(authService.signup(signUpRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto token = authService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }
}
