package com.fileservice.util;

import io.jsonwebtoken.Claims;

/**
 * Интерфейс сервиса для работы с токенами аутентификации
 */
public interface ITokenService {
    /**
     * Генерирует токен для пользователя
     */
    String generateToken(Integer userId, String login);
    
    /**
     * Валидирует токен и возвращает claims
     */
    Claims validateToken(String token);
    
    /**
     * Извлекает ID пользователя из токена
     */
    Integer getUserIdFromToken(String token);
    
    /**
     * Проверяет, истек ли токен
     */
    boolean isTokenExpired(String token);
}

