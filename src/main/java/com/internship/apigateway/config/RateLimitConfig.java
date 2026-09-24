package com.internship.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    // =====================================================
    // DEFAULT REDIS RATE LIMITER
    // =====================================================

    @Bean
    public RedisRateLimiter redisRateLimiter() {

        /*
         * replenishRate = 5 requests per second
         * burstCapacity = 10 requests
         *
         * Route-specific values can still override these
         * through application.properties.
         */

        return new RedisRateLimiter(
                5,
                10
        );
    }


    // =====================================================
    // IP KEY RESOLVER
    // =====================================================

    @Bean
    public KeyResolver ipKeyResolver() {

        return exchange -> {

            if (exchange.getRequest()
                    .getRemoteAddress() == null) {

                return Mono.just("unknown");
            }

            if (exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress() == null) {

                return Mono.just("unknown");
            }

            String ip =
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress();

            return Mono.just(ip);
        };
    }
}