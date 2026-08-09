package com.internship.apigateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        System.out.println("====================================");
        System.out.println("Incoming Request");
        System.out.println("Method : " + exchange.getRequest().getMethod());
        System.out.println("Path   : " + exchange.getRequest().getURI().getPath());
        System.out.println("====================================");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}