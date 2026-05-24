package com.codingshuttle.linkedin.api_gateway.filter;

import com.codingshuttle.linkedin.api_gateway.JWTService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JWTService jwtService;

    public AuthenticationFilter(JWTService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Auth request {}", exchange.getRequest().getURI());
            log.info("hehe");
            final String tokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            final String token = tokenHeader.substring(7);
            System.out.println(token);
            try {
                String userId = jwtService.getUserIdFromToken(token);
                ServerWebExchange mutatedExchange = exchange
                        .mutate()
                        .request(request -> request.header("X-User-Id", userId))
                        .build();
                return chain.filter(mutatedExchange);
            } catch (JwtException jwtException) {
                log.error("JWT Token is invalid{}", jwtException.getLocalizedMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };

    }

    public static class Config{

    }
}
