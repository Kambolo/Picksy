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
            "/account/public",
            "/api/category/public",
            "/api/category-set/public",
            "/api/option/public",
            "/api/profile/public",
            "/api/room/public",
            "/api/decision/public",
            "/ws-room",
            "/ws-poll",
            "/oauth2",
            "/login",
            "/auth-service/v3/api-docs",
            "/category-service/v3/api-docs",
            "/room-service/v3/api-docs",
            "/user-service/v3/api-docs",
            "/decision-service/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Check if path is public
        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                return chain.filter(exchange);
            }
        }

        // Not public - require authentication
        String token = parseJwt(exchange);

        // Unauthorized request
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Get user data form JWT Token
        Long userId = jwtUtils.getUserIdFromToken(token);
        String email = jwtUtils.getStringFromToken(token, "email");
        String role = jwtUtils.getStringFromToken(token, "role");

        // Set additional headers containing user data
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