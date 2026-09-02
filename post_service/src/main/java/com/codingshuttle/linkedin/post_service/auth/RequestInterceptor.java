package com.codingshuttle.linkedin.post_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("METHOD: " + request.getMethod());
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = extractUserIdFromJwt(token); // temp
            AuthContextHolder.setCurrrentUserId(userId);
        }
        return true;
    }

    private Long extractUserIdFromJwt(String token) {
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        String sub = payload.split("\"sub\":\"")[1].split("\"")[0];
        return Long.valueOf(sub);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        AuthContextHolder.removeCurrrentUserId();
    }
}