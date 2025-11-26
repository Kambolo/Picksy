package com.picksy.apigateway.config;

import com.picksy.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtils;

    private final List<String> publicPaths = List.of(
            "/auth/",
            "/api/category/public",
            "/api/option/public",
            "/api/profile/public",
            "/api/room/public",
            "/api/decision/public",
            "/ws-room",
            "/ws-poll",
            "/oauth2",
            "/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        System.out.println("=== AuthGlobalFilter ===");
        System.out.println("Path: " + path);

        // Check if path is public
        boolean isPublic = false;
        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                isPublic = true;
                break;
            }
        }

        // Special case: /auth/account/secure/me is NOT public
        if (path.contains("secure")) {
            isPublic = false;
        }

        if (isPublic) {
            System.out.println("Public endpoint - allowing access");
            return chain.filter(exchange);
        }

        // Not public - require authentication
        String token = parseJwt(exchange);

        if (token == null || !jwtUtils.validateJwtToken(token)) {
            System.out.println("Unauthorized request to " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Add user headers for non-WebSocket paths
        Long userId = jwtUtils.getUserIdFromToken(token);
        String email = jwtUtils.getStringFromToken(token, "email");
        String role = jwtUtils.getStringFromToken(token, "role");

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Email", email != null ? email : "")
                .header("X-User-Role", role != null ? role : "USER")
                .build();

        exchange = exchange.mutate().request(mutatedRequest).build();


        return chain.filter(exchange);
    }

    private String parseJwt(ServerWebExchange exchange) {
        if (exchange.getRequest().getCookies().containsKey("jwt")) {
            return exchange.getRequest().getCookies().getFirst("jwt").getValue();
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
            return authHeaders.get(0).substring(7);
        }

        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}