package com.common.core.migration;

import java.util.List;

/**
 * Интерфейс для чтения файлов миграций
 */
public interface IMigrationFileReader {
    List<MigrationFile> loadMigrationFiles();
}
