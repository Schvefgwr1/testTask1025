package com.fileservice.core.migration;

import com.fileservice.exception.DatabaseException;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для выполнения миграций базы данных
 */
@AllArgsConstructor
public class MigrationService {
    private final Connection connection;
    private final IMigrationRepository migrationRepository;
    private final IMigrationFileReader fileReader;

    /**
     * Инициализирует систему миграций и выполняет все новые миграции
     */
    public void runMigrations() {
        System.out.println("=== Starting database migrations ===");
        
        try {
            checkDatabaseConnection();
            migrationRepository.createMigrationsTableIfNotExists();
            
            //Загружаем все доступные файлы миграций
            List<MigrationFile> migrationFiles = fileReader.loadMigrationFiles();
            System.out.println("Found " + migrationFiles.size() + " migration file(s)");
            
            //Выполняем миграции, которые еще не применены
            int executedCount = 0;
            for (MigrationFile file : migrationFiles) {
                if (!migrationRepository.isMigrationExecuted(file.getVersion())) {
                    executeMigration(file);
                    executedCount++;
                } else {
                    System.out.println("Skipping " + file.getVersion() + " (already executed)");
                }
            }
            
            System.out.println("=== Migrations completed: " + executedCount + " executed ===");
            
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            throw new DatabaseException("Migration process failed", e);
        }
    }

    /**
     * Проверяет подключение к базе данных
     */
    private void checkDatabaseConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new DatabaseException("Database connection is not available");
            }
            System.out.println("Database connection OK");
        } catch (Exception e) {
            throw new DatabaseException("Failed to check database connection", e);
        }
    }

    /**
     * Выполняет одну миграцию в рамках одной транзакции
     * Если хотя бы одна команда упадет - откатываются ВСЕ изменения
     */
    private void executeMigration(MigrationFile file) {
        System.out.println("Executing " + file.getVersion() + ": " + file.getDescription());
        
        boolean success = false;
        boolean originalAutoCommit = true;
        
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            try (Statement stmt = connection.createStatement()) {
                // Разбиваем SQL на команды и очищаем от комментариев
                List<String> sqlCommands = parseSqlCommands(file.getContent());
                
                // Выполняем все команды
                for (String sql : sqlCommands) {
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }

            connection.commit();
            success = true;
            Migration migration = Migration.builder()
                .version(file.getVersion())
                .description(file.getDescription())
                .filename(file.getFilename())
                .checksum(file.getChecksum())
                .success(true)
                .build();
            migrationRepository.saveMigration(migration);
            System.out.println(file.getVersion() + " executed successfully");
            
        } catch (Exception e) {
            System.err.println(file.getVersion() + " failed: " + e.getMessage());
            
            // Откатываем транзакцию при ошибке
            try {
                connection.rollback();
                System.err.println("Transaction rolled back");
            } catch (Exception rollbackException) {
                System.err.println("Failed to rollback transaction: " + rollbackException.getMessage());
            }
            
            // Сохраняем запись о неудачной миграции
            try {
                Migration migration = Migration.builder()
                    .version(file.getVersion())
                    .description(file.getDescription())
                    .filename(file.getFilename())
                    .checksum(file.getChecksum())
                    .success(false)
                    .build();
                migrationRepository.saveMigration(migration);
            } catch (Exception saveException) {
                System.err.println("Failed to save migration record: " + saveException.getMessage());
            }
            
            throw new DatabaseException("Migration " + file.getVersion() + " failed", e);
        } finally {
            // Восстанавливаем исходный режим автокоммита
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (Exception e) {
                System.err.println("Failed to restore autoCommit mode: " + e.getMessage());
            }
        }
    }
    
    /**
     * Парсит SQL контент на отдельные команды, удаляя комментарии
     */
    private List<String> parseSqlCommands(String sqlContent) {
        List<String> commands = new ArrayList<>();
        StringBuilder currentCommand = new StringBuilder();
        
        String[] lines = sqlContent.split("\n");
        
        for (String line : lines) {
            // Удаляем комментарии (все после --)
            int commentIndex = line.indexOf("--");
            if (commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }
            
            // Пропускаем пустые строки
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            
            // Добавляем строку к текущей команде
            currentCommand.append(line).append("\n");
            
            // Если строка заканчивается на ";", значит команда завершена
            if (trimmedLine.endsWith(";")) {
                String command = currentCommand.toString().trim();
                // Убираем последнюю точку с запятой
                if (command.endsWith(";")) {
                    command = command.substring(0, command.length() - 1).trim();
                }
                if (!command.isEmpty()) {
                    commands.add(command);
                }
                currentCommand = new StringBuilder();
            }
        }
        
        // Если осталась незавершенная команда, добавляем её
        String remaining = currentCommand.toString().trim();
        if (!remaining.isEmpty()) {
            commands.add(remaining);
        }
        
        return commands;
    }
}

