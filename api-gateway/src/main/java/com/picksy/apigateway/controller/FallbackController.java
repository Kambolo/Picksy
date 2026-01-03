package com.picksy.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController{
    @RequestMapping("/fallback/user")
    public ResponseEntity<String> userFallback() {
        return fallback("User");
    }

    @RequestMapping("/fallback/room")
    public ResponseEntity<String> roomFallback() {
        return fallback("Room");
    }

    @RequestMapping("/fallback/auth")
    public ResponseEntity<String> authFallback() {
        return fallback("Auth");
    }

    @RequestMapping("/fallback/decision")
    public ResponseEntity<String> decisionFallback() {
        return fallback("Decision");
    }

    @RequestMapping("/fallback/category")
    public ResponseEntity<String> categoryFallback() {
        return fallback("Category");
    }

    private ResponseEntity<String> fallback(String serviceName) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(serviceName + "Service is unavailable. Please try again later.");
    }
}
