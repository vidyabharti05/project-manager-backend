package com.projectmanager.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET =
            "my-super-secret-key-for-ai-project-manager-2026";

    private static final long TOKEN_EXPIRATION =
            1000L * 60 * 60 * 24;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
            now.getTime() + TOKEN_EXPIRATION
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}