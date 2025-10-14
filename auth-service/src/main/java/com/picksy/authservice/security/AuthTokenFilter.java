package com.picksy.authservice.security;

import com.picksy.authservice.Util.JwtUtil;
import com.picksy.authservice.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();

        // Skip filter for public endpoints
        if (path.startsWith("/auth/signin") || path.startsWith("/auth/signup") || path.startsWith("/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);

            System.out.println("AuthTokenFilter - Path: " + path);
            System.out.println("AuthTokenFilter - Token: " + (jwt != null ? "Found" : "Not found"));

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                System.out.println("AuthTokenFilter - Username: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("AuthTokenFilter - Authentication set successfully");
            } else {
                System.out.println("AuthTokenFilter - Invalid or missing token");
            }
        } catch (Exception e) {
            System.out.println("AuthTokenFilter - Error: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{ \"message\": \"Invalid or expired token\" }");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        // Check cookies first
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    System.out.println("AuthTokenFilter - JWT found in cookies");
                    return cookie.getValue();
                }
            }
        }

        // Check Authorization header
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            System.out.println("AuthTokenFilter - JWT found in Authorization header");
            return headerAuth.substring(7);
        }

        System.out.println("AuthTokenFilter - JWT not found in cookies or header");
        return null;
    }
}