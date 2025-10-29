package com.fileservice.util;

/**
 * Интерфейс сервиса для хеширования паролей и токенов
 */
public interface IPasswordHasher {
    /**
     * Хеширует пароль с солью
     */
    String hashPassword(String password);
    
    /**
     * Проверяет пароль против хеша
     */
    boolean verifyPassword(String password, String hashedPassword);
    
    /**
     * Хеширует токен (для хранения в БД)
     */
    String hashToken(String token);
}

