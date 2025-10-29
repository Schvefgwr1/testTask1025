package com.fileservice.repository;

import com.fileservice.model.User;

/**
 * Интерфейс репозитория для работы с пользователями
 */
public interface IUserRepository {
    /**
     * Находит пользователя по логину
     */
    User findByLogin(String login);
    
    /**
     * Создает нового пользователя
     */
    User create(String login, String passwordHash);
    
    /**
     * Проверяет, существует ли пользователь с данным логином
     */
    boolean existsByLogin(String login);
}

