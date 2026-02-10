package com.common.core.migration;

import java.util.List;

/**
 * Интерфейс репозитория для работы с миграциями базы данных
 */
public interface IMigrationRepository {
    void createMigrationsTableIfNotExists();

    boolean isMigrationExecuted(String version);

    void saveMigration(Migration migration);

    List<Migration> getAllMigrations();
}
