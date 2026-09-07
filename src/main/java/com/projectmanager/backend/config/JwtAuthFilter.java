package com.projectmanager.backend.config;

import java.io.IOException;
import java.util.Collections;

import com.projectmanager.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (
            authHeader == null ||
            !authHeader.startsWith("Bearer ")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isBlank()) {
            writeUnauthorizedResponse(
                response,
                "JWT token is missing"
            );
            return;
        }

        try {
            String username = jwtService.extractUsername(token);

            if (
                username != null &&
                SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null
            ) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                        );

                SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            SecurityContextHolder.clearContext();

            writeUnauthorizedResponse(
                response,
                "Invalid or expired JWT token"
            );
        }
    }

    private void writeUnauthorizedResponse(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
            HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
            "{\"error\":\"" + message + "\"}"
        );
    }
}