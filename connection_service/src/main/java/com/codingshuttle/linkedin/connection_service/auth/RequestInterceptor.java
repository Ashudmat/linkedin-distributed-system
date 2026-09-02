package com.codingshuttle.linkedin.connection_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String userId = request.getHeader("X-User-Id");

        if (userId == null || userId.isBlank()) {
            return true;
        }
        AuthContextHolder.setCurrrentUserId(Long.valueOf(userId));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        AuthContextHolder.removeCurrrentUserId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
