package com.codingshuttle.linkedin.post_service.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = AuthContextHolder.getCurrrentUserId();
        requestTemplate.header("X-User-Id", String.valueOf(userId));
    }
}
