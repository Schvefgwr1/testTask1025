package com.fileservice.repository;

import com.fileservice.model.Session;

/**
 * Интерфейс репозитория для работы с сессиями
 */
public interface ISessionRepository {
    /**
     * Создает новую сессию
     */
    Session create(Integer userId, String tokenHash);
    
    /**
     * Находит сессию по хешу токена
     */
    Session findByTokenHash(String tokenHash);
    
    /**
     * Деактивирует сессию
     */
    void deactivateSession(String tokenHash);
}

