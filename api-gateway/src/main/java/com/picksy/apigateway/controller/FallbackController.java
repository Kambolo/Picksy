package com.picksy.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController{
    @RequestMapping("/fallback/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service is unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/room")
    public ResponseEntity<String> roomFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Room Service is unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/auth")
    public ResponseEntity<String> authFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth Service is unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/decision")
    public ResponseEntity<String> decisionFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Decision Service is unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/category")
    public ResponseEntity<String> categoryFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Category Service is unavailable. Please try again later.");
    }
}
