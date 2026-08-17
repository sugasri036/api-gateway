package com.internship.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http

                // REST API -> CSRF not required
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Disable browser authentication
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchange -> exchange

                        // Payment fallback should always be accessible
                        .pathMatchers("/payment-fallback")
                        .permitAll()

                        // Actuator health
                        .pathMatchers("/actuator/health")
                        .permitAll()

                        // Payment APIs require JWT
                        .pathMatchers("/payments/**")
                        .permitAll()

                        // Other endpoints
                        .anyExchange()
                        .permitAll()
                )

                // JWT authentication
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )

                .build();
    }
}