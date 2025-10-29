package com.fileservice.core.migration;

import java.util.List;

/**
 * Интерфейс репозитория для работы с миграциями базы данных
 */
public interface IMigrationRepository {
    /**
     * Создает таблицу миграций если её нет
     */
    void createMigrationsTableIfNotExists();
    
    /**
     * Проверяет, была ли выполнена миграция
     */
    boolean isMigrationExecuted(String version);
    
    /**
     * Сохраняет информацию о выполненной миграции
     */
    void saveMigration(Migration migration);
    
    /**
     * Получает все выполненные миграции
     */
    List<Migration> getAllMigrations();
}

