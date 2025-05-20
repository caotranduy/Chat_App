package com.duay.AuthService.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    private final String SECRET = "secret-key";

    // @SuppressWarnings("deprecation")
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 ngày
            .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
            .compact();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts
            .parser()
            .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }
}

