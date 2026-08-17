package com.internship.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.core.Ordered;

import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


@Component
public class JwtIdentityFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder
                .getContext()

                .flatMap(context -> {

                    Authentication authentication =
                            context.getAuthentication();


                    // =====================================================
                    // CHECK AUTHENTICATION
                    // =====================================================

                    if (authentication == null ||
                            !authentication.isAuthenticated()) {

                        return chain.filter(exchange);
                    }


                    // =====================================================
                    // GET JWT
                    // =====================================================

                    Object principal =
                            authentication.getPrincipal();


                    if (!(principal instanceof Jwt jwt)) {

                        return chain.filter(exchange);
                    }


                    // =====================================================
                    // GET USER ID FROM JWT
                    // =====================================================

                    String userIdFromClaim =
                            jwt.getClaimAsString("userId");


                    String userId;

                    if (userIdFromClaim != null &&
                            !userIdFromClaim.isBlank()) {

                        userId = userIdFromClaim;

                    } else {

                        userId = jwt.getSubject();
                    }


                    // =====================================================
                    // CHECK USER ID
                    // =====================================================

                    if (userId == null ||
                            userId.isBlank()) {

                        return chain.filter(exchange);
                    }


                    /*
                     * At this point userId will never be changed again.
                     *
                     * Therefore Java considers it effectively final
                     * and it can safely be used inside the lambda below.
                     */


                    // =====================================================
                    // ADD AUTHENTICATED USER ID TO REQUEST
                    // =====================================================

                    ServerHttpRequest request =
                            exchange
                                    .getRequest()
                                    .mutate()
                                    .headers(headers -> {

                                        /*
                                         * Remove any X-User-Id sent
                                         * by the client.
                                         *
                                         * This prevents the client from
                                         * impersonating another user.
                                         */

                                        headers.remove(
                                                USER_ID_HEADER
                                        );


                                        /*
                                         * Add the user ID obtained
                                         * from the verified JWT.
                                         */

                                        headers.set(
                                                USER_ID_HEADER,
                                                userId
                                        );

                                    })
                                    .build();


                    // =====================================================
                    // CREATE MODIFIED EXCHANGE
                    // =====================================================

                    ServerWebExchange modifiedExchange =
                            exchange
                                    .mutate()
                                    .request(request)
                                    .build();


                    // =====================================================
                    // CONTINUE REQUEST
                    // =====================================================

                    return chain.filter(
                            modifiedExchange
                    );

                })


                // =====================================================
                // NO SECURITY CONTEXT
                // =====================================================

                .switchIfEmpty(
                        chain.filter(exchange)
                );
    }


    @Override
    public int getOrder() {

        return Ordered.LOWEST_PRECEDENCE;
    }
}