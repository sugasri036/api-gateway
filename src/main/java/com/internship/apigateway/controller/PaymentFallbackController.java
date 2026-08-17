package com.internship.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentFallbackController {

    @RequestMapping("/payment-fallback")
    public ResponseEntity<Map<String, Object>> paymentFallback() {

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Payment service is currently unavailable");
        response.put("status", "FAILED");
        response.put("fallback", true);
        response.put("service", "payment-service");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}