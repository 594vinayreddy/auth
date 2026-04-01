package com.ewallet.auth_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    @SuppressWarnings("unused")
    private String secret;

    @Value("${jwt.expiration-ms}")
    @SuppressWarnings("unused")
    private long expirationMs;

    public String generateToken(String email, String role) {
        // Implement JWT token generation logic using the secret and expiration
        // You can use libraries like io.jsonwebtoken.Jwts for this
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
