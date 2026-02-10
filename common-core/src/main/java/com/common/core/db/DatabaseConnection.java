package com.common.core.db;

import com.common.core.config.Config;
import com.common.core.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Менеджер подключения к базе данных PostgreSQL
 */
public class DatabaseConnection {
    private final Config config;
    private Connection connection;

    public DatabaseConnection(Config config) {
        this.config = config;
    }

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
