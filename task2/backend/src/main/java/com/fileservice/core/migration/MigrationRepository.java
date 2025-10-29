package com.fileservice.core.migration;

import com.fileservice.exception.DatabaseException;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с таблицей миграций
 */
@AllArgsConstructor
public class MigrationRepository implements IMigrationRepository {
    private final Connection connection;

    /**
     * Создает таблицу миграций если её нет
     */
    public void createMigrationsTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS schema_migrations (
                id SERIAL PRIMARY KEY,
                version VARCHAR(50) UNIQUE NOT NULL,
                description VARCHAR(255) NOT NULL,
                filename VARCHAR(255) NOT NULL,
                checksum VARCHAR(32) NOT NULL,
                executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                success BOOLEAN NOT NULL DEFAULT true
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица schema_migrations готова");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create migrations table", e);
        }
    }

    /**
     * Проверяет, была ли выполнена миграция
     */
    public boolean isMigrationExecuted(String version) {
        String sql = "SELECT COUNT(*) FROM schema_migrations WHERE version = ? AND success = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check migration status", e);
        }
    }

    /**
     * Сохраняет информацию о выполненной миграции
     */
    public void saveMigration(Migration migration) {
        String sql = """
            INSERT INTO schema_migrations (version, description, filename, checksum, executed_at, success)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, migration.getVersion());
            stmt.setString(2, migration.getDescription());
            stmt.setString(3, migration.getFilename());
            stmt.setString(4, migration.getChecksum());
            stmt.setBoolean(5, migration.getSuccess());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save migration record", e);
        }
    }

    /**
     * Получает все выполненные миграции
     */
    public List<Migration> getAllMigrations() {
        String sql = "SELECT * FROM schema_migrations ORDER BY version";
        List<Migration> migrations = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Migration migration = Migration.builder()
                    .id(rs.getInt("id"))
                    .version(rs.getString("version"))
                    .description(rs.getString("description"))
                    .filename(rs.getString("filename"))
                    .checksum(rs.getString("checksum"))
                    .executedAt(rs.getTimestamp("executed_at"))
                    .success(rs.getBoolean("success"))
                    .build();
                migrations.add(migration);
            }
            
            return migrations;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get migrations", e);
        }
    }
}

