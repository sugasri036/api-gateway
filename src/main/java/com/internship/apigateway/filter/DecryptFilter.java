package com.internship.apigateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.apigateway.config.CachedBodyRequestDecorator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;

//@Component("decryptFilter")
public class DecryptFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        /*
         * Only process requests that actually contain a body.
         *
         * GET requests such as:
         * GET /payments
         * GET /payments/order/{orderId}
         * should NOT go through request decryption.
         */

        HttpMethod method = exchange.getRequest().getMethod();

        if (method == null ||
                method == HttpMethod.GET ||
                method == HttpMethod.DELETE) {

            return chain.filter(exchange);
        }

        return DataBufferUtils.join(
                exchange.getRequest().getBody()
        ).flatMap(dataBuffer -> {

            try {

                byte[] bytes =
                        new byte[dataBuffer.readableByteCount()];

                dataBuffer.read(bytes);

                DataBufferUtils.release(dataBuffer);

                String body =
                        new String(
                                bytes,
                                StandardCharsets.UTF_8
                        );

                System.out.println();
                System.out.println("================================");
                System.out.println("GATEWAY - ORIGINAL REQUEST");
                System.out.println("================================");

                System.out.println(body);

                // -------------------------------------------------
                // Parse outer JSON
                // -------------------------------------------------

                JsonNode root =
                        objectMapper.readTree(body);

                /*
                 * Expected encrypted request:
                 *
                 * {
                 *   "data": "{\"userId\":\"user123\",\"amount\":500}"
                 * }
                 */

                JsonNode dataNode =
                        root.get("data");

                if (dataNode == null) {

                    System.out.println(
                            "No 'data' field found."
                    );

                    return chain.filter(exchange);
                }

                // -------------------------------------------------
                // Get encrypted data
                // -------------------------------------------------

                String encryptedData =
                        dataNode.asText();

                System.out.println();
                System.out.println(
                        "Wrapped Data:"
                );

                System.out.println(
                        encryptedData
                );

                // -------------------------------------------------
                // SIMULATED DECRYPTION
                //
                // Currently your encryption is:
                //
                // originalResponse.toUpperCase()
                //
                // Therefore decryption is:
                //
                // encrypted.toLowerCase()
                // -------------------------------------------------

                String decryptedData =
                        encryptedData.toLowerCase();

                System.out.println();
                System.out.println(
                        "Decrypted Data:"
                );

                System.out.println(
                        decryptedData
                );

                // -------------------------------------------------
                // IMPORTANT
                //
                // Do NOT forward:
                //
                // {
                //     "data": "..."
                // }
                //
                // Forward the actual decrypted JSON:
                //
                // {
                //     "userId": "user123",
                //     "amount": 500
                // }
                // -------------------------------------------------

                JsonNode decryptedJson =
                        objectMapper.readTree(
                                decryptedData
                        );

                String finalBody =
                        objectMapper.writeValueAsString(
                                decryptedJson
                        );

                System.out.println();
                System.out.println(
                        "================================"
                );

                System.out.println(
                        "GATEWAY - FORWARDING REQUEST"
                );

                System.out.println(
                        "================================"
                );

                System.out.println(
                        finalBody
                );

                // -------------------------------------------------
                // Create new request with decrypted body
                // -------------------------------------------------

                CachedBodyRequestDecorator decorator =
                        new CachedBodyRequestDecorator(
                                exchange.getRequest(),
                                finalBody
                        );

                ServerWebExchange mutatedExchange =
                        exchange.mutate()
                                .request(decorator)
                                .build();

                // -------------------------------------------------
                // Forward request
                // -------------------------------------------------

                return chain.filter(
                        mutatedExchange
                );

            } catch (Exception e) {

                DataBufferUtils.release(dataBuffer);

                e.printStackTrace();

                return Mono.error(e);
            }
        });
    }


    @PostConstruct
    public void init() {

        System.out.println(
                "DecryptFilter Loaded"
        );
    }


    @Override
    public int getOrder() {

        return 1;
    }
}