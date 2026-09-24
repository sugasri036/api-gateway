package com.internship.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

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

                // =====================================================
                // REST API
                // =====================================================

                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .httpBasic(
                        ServerHttpSecurity.HttpBasicSpec::disable
                )

                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )

                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeExchange(exchange -> exchange
                         // -------------------------------------------------
                        // CORS PREFLIGHT
                       // -------------------------------------------------

                      .pathMatchers(
                              HttpMethod.OPTIONS,
                               "/**"
                       )
                      .permitAll()


                        // -------------------------------------------------
                        // ACTUATOR HEALTH
                        // -------------------------------------------------

                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/health/**"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // PAYMENT FALLBACK
                        // -------------------------------------------------

                        .pathMatchers(
                                "/payment-fallback"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // AUTH SERVICE - PUBLIC
                        // -------------------------------------------------

                        .pathMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/forgot-password/verify",
                                "/api/auth/forgot-password/reset"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // GOOGLE OAUTH - PUBLIC
                        // -------------------------------------------------

                        .pathMatchers(
                                "/oauth2/**",
                                "/login/**"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // FUND READ OPERATIONS - PUBLIC
                        // -------------------------------------------------

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/funds",
                                "/api/funds/**"
                        )
                        .permitAll()

                        // -------------------------------------------------
                        // FUND WRITE OPERATIONS - AUTHENTICATED
                        // -------------------------------------------------

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/funds",
                                "/api/funds/**"
                        )
                        .authenticated()

                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/funds/**"
                        )
                        .authenticated()

                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/funds/**"
                        )
                        .authenticated()

                        // -------------------------------------------------
                        // ORDER SERVICE
                        // -------------------------------------------------

                        .pathMatchers(
                                "/api/orders/**"
                        )
                        .authenticated()

                        // -------------------------------------------------
                        // PAYMENT SERVICE
                        // -------------------------------------------------

                        .pathMatchers(
                                "/api/payments/**"
                        )
                        .authenticated()

                        // -------------------------------------------------
                        // OTP
                        // -------------------------------------------------
                        //
                        // OTP is used internally by Auth Service.
                        // It should NOT be exposed publicly through
                        // the API Gateway.
                        //

                        .pathMatchers(
                                "/api/otp/**"
                        )
                        .denyAll()

                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------

                        .anyExchange()
                        .authenticated()
                )

                // =====================================================
                // JWT
                // =====================================================

                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt -> {
                                        }
                                )
                )

                .build();
    }
}