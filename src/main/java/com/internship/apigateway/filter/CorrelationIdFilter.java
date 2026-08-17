package com.internship.apigateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER =
            "X-Correlation-ID";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String correlationId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(CORRELATION_ID_HEADER);

        // If client did not provide a correlation ID,
        // generate a new one.
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        // Add correlation ID to the request going downstream
        ServerWebExchange modifiedExchange =
                exchange.mutate()
                        .request(
                                exchange.getRequest()
                                        .mutate()
                                        .header(
                                                CORRELATION_ID_HEADER,
                                                finalCorrelationId
                                        )
                                        .build()
                        )
                        .build();

        // Also return the correlation ID to the client
        exchange.getResponse()
                .getHeaders()
                .set(
                        CORRELATION_ID_HEADER,
                        finalCorrelationId
                );

        System.out.println(
                "[TRACE] Gateway received request | "
                        + "Correlation-ID: "
                        + finalCorrelationId
                        + " | "
                        + exchange.getRequest().getMethod()
                        + " "
                        + exchange.getRequest().getURI()
                );

        return chain.filter(modifiedExchange)
                .doOnSuccess(
                        result -> System.out.println(
                                "[TRACE] Gateway completed request | "
                                        + "Correlation-ID: "
                                        + finalCorrelationId
                        )
                )
                .doOnError(
                        error -> System.out.println(
                                "[TRACE] Gateway request failed | "
                                        + "Correlation-ID: "
                                        + finalCorrelationId
                                        + " | Error: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}