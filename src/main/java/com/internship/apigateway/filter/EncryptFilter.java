package com.internship.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//@Component("encryptFilter")
public class EncryptFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        ServerHttpResponse originalResponse = exchange.getResponse();

        ServerHttpResponseDecorator decoratedResponse =
                new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(
                            org.reactivestreams.Publisher<? extends DataBuffer> body) {

                        return DataBufferUtils.join(body)
                                .flatMap(dataBuffer -> {

                                    byte[] bytes =
                                            new byte[dataBuffer.readableByteCount()];

                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);

                                    String originalResponseBody =
                                            new String(bytes, StandardCharsets.UTF_8);

                                    System.out.println("================================");
                                    System.out.println("Original Response : "
                                            + originalResponseBody);

                                    try {

                                        // Simulated encryption
                                        String encrypted =
                                                originalResponseBody.toUpperCase();

                                        Map<String, String> responseMap =
                                                new HashMap<>();

                                        responseMap.put("data", encrypted);

                                        String finalResponse =
                                                objectMapper.writeValueAsString(
                                                        responseMap);

                                        System.out.println(
                                                "Encrypted Response : "
                                                        + encrypted);

                                        System.out.println(
                                                "Final Response : "
                                                        + finalResponse);

                                        byte[] responseBytes =
                                                finalResponse.getBytes(
                                                        StandardCharsets.UTF_8);

                                        DataBuffer buffer =
                                                originalResponse
                                                        .bufferFactory()
                                                        .wrap(responseBytes);

                                        originalResponse.getHeaders()
                                                .remove("Content-Length");

                                        return super.writeWith(
                                                Mono.just(buffer));

                                    } catch (Exception e) {
                                        return Mono.error(e);
                                    }
                                });
                    }
                };

        return chain.filter(
                exchange.mutate()
                        .response(decoratedResponse)
                        .build()
        );
    }

    @Override
    public int getOrder() {
        return -2;
}
    }
