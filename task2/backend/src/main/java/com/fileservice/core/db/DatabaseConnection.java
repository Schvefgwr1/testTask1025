package com.fileservice.core.db;

import com.fileservice.core.config.Config;
import com.fileservice.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Менеджер подключения к базе данных
 * Теперь использует DI вместо статических методов
 */
public class DatabaseConnection {
    private final Config config;
    private Connection connection;

    public DatabaseConnection(Config config) {
        this.config = config;
    }

    /**
     * Получает подключение к БД (создает если еще нет)
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(
                        config.getDbUrl(),
                        config.getDbUser(),
                        config.getDbPassword()
                    );
                    System.out.println("Database connection established");
                } catch (ClassNotFoundException e) {
                    throw new DatabaseException("PostgreSQL Driver not found", e);
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get database connection", e);
        }
    }

    /**
     * Закрывает подключение к БД
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

