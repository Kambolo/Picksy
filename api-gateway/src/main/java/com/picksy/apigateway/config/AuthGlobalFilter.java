package com.picksy.apigateway.config;


import com.picksy.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
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
            "/api/profile/public"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        System.out.println("AuthGlobalFilter - Cookies: " + exchange.getRequest().getCookies());

        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                System.out.println("AuthGlobalFilter - Public endpoint: " + path);
                return chain.filter(exchange);
            }
        }

        String token = parseJwt(exchange);
        System.out.println("AuthGlobalFilter - Token: " + token);

        if (token == null || !jwtUtils.validateJwtToken(token)) {
            System.out.println("AuthGlobalFilter - Unauthorized request to " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtUtils.getUsernameFromToken(token);
        exchange.getRequest().mutate()
                .header("X-User-Name", username)
                .build();

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
