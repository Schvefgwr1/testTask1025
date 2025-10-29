package com.fileservice.util;

import com.fileservice.core.config.Config;
import com.fileservice.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Утилита для работы с JWT токенами
 */
public class JwtUtil implements ITokenService {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(Config config) {
        this.secretKey = Keys.hmacShaKeyFor(config.getJwtSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = config.getJwtExpirationMs();
    }

    /**
     * Генерирует JWT токен для пользователя
     */
    public String generateToken(Integer userId, String login) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("login", login)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Валидирует токен и возвращает claims
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid or expired token", e);
        }
    }

    /**
     * Извлекает ID пользователя из токена
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * Проверяет, истек ли токен
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

