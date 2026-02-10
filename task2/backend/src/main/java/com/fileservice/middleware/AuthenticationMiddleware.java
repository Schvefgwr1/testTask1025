package com.fileservice.middleware;

import com.common.core.http.Middleware;
import com.common.core.http.MiddlewareChain;
import com.fileservice.exception.AuthenticationException;
import com.fileservice.exception.InvalidTokenException;
import com.fileservice.model.Session;
import com.fileservice.repository.ISessionRepository;
import com.fileservice.util.IPasswordHasher;
import com.fileservice.util.ITokenService;
import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * Middleware для аутентификации запросов
 */
@AllArgsConstructor
public class AuthenticationMiddleware implements Middleware {
    private final ISessionRepository sessionRepository;
    private final ITokenService tokenService;
    private final IPasswordHasher passwordHasher;

    @Override
    public void handle(HttpExchange exchange, MiddlewareChain next) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        int userId;
        try {
            Claims claims = tokenService.validateToken(token);
            userId = Integer.parseInt(claims.getSubject());

            if (tokenService.isTokenExpired(token)) {
                throw new AuthenticationException("Token has expired");
            }
        } catch (InvalidTokenException e) {
            throw new AuthenticationException("Invalid token: " + e.getMessage());
        }

        // Проверяем, что сессия активна в базе данных
        String tokenHash = passwordHasher.hashToken(token);
        Session session = sessionRepository.findByTokenHash(tokenHash);
        
        if (session == null || !session.getIsActive()) {
            throw new AuthenticationException("Session not found or inactive");
        }

        exchange.setAttribute("userId", userId);
        next.proceed(exchange);
    }
}

