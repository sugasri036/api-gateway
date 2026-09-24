package com.internship.apigateway.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${FRONTEND_URL}")
    private String frontendUrl;


    // ============================================================
    // CORS CONFIGURATION
    // ============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(frontendUrl)
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http

                // ====================================================
                // CSRF
                // ====================================================

                .csrf(ServerHttpSecurity.CsrfSpec::disable)


                // ====================================================
                // CORS
                // ====================================================

                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()
                ))


                // ====================================================
                // DISABLE BASIC AUTH
                // ====================================================

                .httpBasic(
                        ServerHttpSecurity.HttpBasicSpec::disable
                )


                // ====================================================
                // DISABLE FORM LOGIN
                // ====================================================

                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )


                // ====================================================
                // AUTHORIZATION
                // ====================================================

                .authorizeExchange(exchange -> exchange


                        // ------------------------------------------------
                        // CORS PREFLIGHT
                        // ------------------------------------------------

                        .pathMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // ACTUATOR HEALTH
                        // ------------------------------------------------

                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/health/**"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // PAYMENT FALLBACK
                        // ------------------------------------------------

                        .pathMatchers(
                                "/payment-fallback"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // AUTH SERVICE - PUBLIC
                        // ------------------------------------------------

                        .pathMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/forgot-password/verify",
                                "/api/auth/forgot-password/reset"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // GOOGLE OAUTH - PUBLIC
                        // ------------------------------------------------

                        .pathMatchers(
                                "/oauth2/**",
                                "/login/**"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // FUND GET - PUBLIC
                        // ------------------------------------------------

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/funds",
                                "/api/funds/**"
                        )
                        .permitAll()


                        // ------------------------------------------------
                        // FUND POST - AUTHENTICATED
                        // ------------------------------------------------

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/funds",
                                "/api/funds/**"
                        )
                        .authenticated()


                        // ------------------------------------------------
                        // FUND PUT - AUTHENTICATED
                        // ------------------------------------------------

                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/funds/**"
                        )
                        .authenticated()


                        // ------------------------------------------------
                        // FUND DELETE - AUTHENTICATED
                        // ------------------------------------------------

                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/api/funds/**"
                        )
                        .authenticated()


                        // ------------------------------------------------
                        // ORDER SERVICE - AUTHENTICATED
                        // ------------------------------------------------

                        .pathMatchers(
                                "/api/orders/**"
                        )
                        .authenticated()


                        // ------------------------------------------------
                        // PAYMENT SERVICE - AUTHENTICATED
                        // ------------------------------------------------

                        .pathMatchers(
                                "/api/payments/**"
                        )
                        .authenticated()


                        // ------------------------------------------------
                        // OTP
                        // ------------------------------------------------
                        // OTP is an internal Auth -> OTP communication
                        // and must not be publicly accessible.
                        // ------------------------------------------------

                        .pathMatchers(
                                "/api/otp/**"
                        )
                        .denyAll()


                        // ------------------------------------------------
                        // EVERYTHING ELSE
                        // ------------------------------------------------

                        .anyExchange()
                        .authenticated()
                )


                // ====================================================
                // JWT RESOURCE SERVER
                // ====================================================

                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(
                                jwt -> {
                                }
                        )
                )


                // ====================================================
                // BUILD
                // ====================================================

                .build();
    }
}