package com.internship.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.apigateway.config.CachedBodyRequestDecorator;
import com.internship.apigateway.dto.RequestWrapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Component("decryptFilter")
public class DecryptFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {

                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);

                    String body = new String(bytes, StandardCharsets.UTF_8);

                    // Release buffer
                    DataBufferUtils.release(dataBuffer);

                    System.out.println("================================");
                    System.out.println("Request Body:");
                    System.out.println(body);
                    System.out.println("================================");

                    try {

                        // Convert JSON -> Java Object
                        RequestWrapper request =
                                objectMapper.readValue(body, RequestWrapper.class);

                        String encrypted = request.getData();

                        System.out.println("Encrypted Data : " + encrypted);

                        // Simulate decryption
                        String decrypted = encrypted.toLowerCase();

                        System.out.println("Decrypted Data : " + decrypted);

                        // Update object
                        request.setData(decrypted);

                        // Convert back to JSON
                        String modifiedBody =
                                objectMapper.writeValueAsString(request);

                        System.out.println("Modified Body:");
                        System.out.println(modifiedBody);

                        // Create new request with modified body
                        CachedBodyRequestDecorator decorator =
                                new CachedBodyRequestDecorator(
                                        exchange.getRequest(),
                                        modifiedBody
                                );

                        // Replace request
                        ServerWebExchange mutatedExchange =
                                exchange.mutate()
                                        .request(decorator)
                                        .build();

                        // Continue filter chain
                        return chain.filter(mutatedExchange);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }

                });
    }
    @PostConstruct
public void init() {
    System.out.println("DecryptFilter Loaded");
}

    @Override
    public int getOrder() {
        return 1;
    }
}