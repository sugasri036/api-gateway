package com.internship.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("userRateLimitKeyResolver")
public class UserRateLimitKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(
            org.springframework.web.server.ServerWebExchange exchange) {

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        if (userId == null || userId.isBlank()) {
            return Mono.just("anonymous");
        }

        return Mono.just(userId);
    }
}