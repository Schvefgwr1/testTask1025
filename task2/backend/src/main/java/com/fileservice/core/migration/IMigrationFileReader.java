package com.fileservice.core.migration;

import java.util.List;

/**
 * Интерфейс для чтения файлов миграций
 */
public interface IMigrationFileReader {
    /**
     * Загружает все доступные файлы миграций
     */
    List<MigrationFile> loadMigrationFiles();
}

