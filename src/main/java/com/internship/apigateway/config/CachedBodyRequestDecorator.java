package com.internship.apigateway.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;

import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

public class CachedBodyRequestDecorator
        extends ServerHttpRequestDecorator {

    private final byte[] body;


    public CachedBodyRequestDecorator(
            ServerHttpRequest request,
            String body) {

        super(request);

        this.body =
                body.getBytes(
                        StandardCharsets.UTF_8
                );
    }


    @Override
    public HttpHeaders getHeaders() {

        HttpHeaders headers =
                new HttpHeaders();

        headers.putAll(
                super.getHeaders()
        );

        headers.remove(
                HttpHeaders.CONTENT_LENGTH
        );

        headers.setContentLength(
                body.length
        );

        headers.set(
                HttpHeaders.CONTENT_TYPE,
                "application/json"
        );

        return headers;
    }


    @Override
    public Flux<DataBuffer> getBody() {

        return Flux.defer(() -> {

            DataBuffer buffer =
                    new DefaultDataBufferFactory()
                            .wrap(body);

            return Flux.just(buffer);
        });
    }
}