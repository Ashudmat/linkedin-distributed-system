package com.codingshuttle.linkedin.user_service.security;

import com.codingshuttle.linkedin.user_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    //generate access token
    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("profileImageUrl",user.getProfileImageUrl())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofDays(1).toMillis()))
                .signWith(getSecretKey())
                .compact();
    }

//    public String extractUserId(String token) {
//        return Jwts.parser()
//                .verifyWith(getSecretKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
}
