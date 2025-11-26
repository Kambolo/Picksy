package com.picksy.apigateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController{
    @RequestMapping("/fallback/user")
    public Mono<String> userFallback() {
        return Mono.just("{\"error\": \"User service is not available!\"}");
    }

    @RequestMapping("/fallback/room")
    public Mono<String> roomFallback() {
        return Mono.just("{\"error\": \"Room service is not available!\"}");
    }

    @RequestMapping("/fallback/auth")
    public Mono<String> authFallback() {
        return Mono.just("{\"error\": \"Auth service is not available!\"}");
    }

    @RequestMapping("/fallback/decision")
    public Mono<String> decisionFallback() {
        return Mono.just("{\"error\": \"Decision service is not available!\"}");
    }
}
