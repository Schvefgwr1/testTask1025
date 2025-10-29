package com.fileservice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Утилита для работы с паролями и хешированием
 */
public class PasswordUtil implements IPasswordHasher {
    private final SecureRandom random;
    private static final int SALT_LENGTH = 16;

    public PasswordUtil() {
        this.random = new SecureRandom();
    }

    /**
     * Хеширует пароль с солью
     */
    public String hashPassword(String password) {
        try {
            // Генерация соли
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Хеширование пароля с солью
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Объединяем соль и хеш
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Проверяет пароль против хеша
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        try {
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            // Извлекаем соль
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // Хешируем введенный пароль с той же солью
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Извлекаем сохраненный хеш
            byte[] storedHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedHash, 0, storedHash.length);
            
            // Сравниваем хеши
            return MessageDigest.isEqual(hashedInput, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Хеширует токен (для хранения в БД)
     */
    public String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}

