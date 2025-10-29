package com.fileservice.core.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурация приложения
 * Читает настройки из application.properties с возможностью переопределения через переменные окружения
 */
@Getter
public class Config {
    private final Properties properties;

    // Значения конфигурации
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final int serverPort;
    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final String fileStoragePath;
    private final int fileRetentionDays;

    public Config() {
        this.properties = new Properties();
        loadProperties();
        
        // Загружаем значения с возможностью переопределения через env
        this.dbUrl = getProperty("db.url", "DB_URL");
        this.dbUser = getProperty("db.user", "DB_USER");
        this.dbPassword = getProperty("db.password", "DB_PASSWORD");
        this.serverPort = Integer.parseInt(getProperty("server.port", "SERVER_PORT"));
        this.jwtSecret = getProperty("jwt.secret", "JWT_SECRET");
        this.jwtExpirationMs = Long.parseLong(getProperty("jwt.expiration.ms", "JWT_EXPIRATION_MS"));
        this.fileStoragePath = getProperty("file.storage.path", "FILE_STORAGE_PATH");
        this.fileRetentionDays = Integer.parseInt(getProperty("file.retention.days", "FILE_RETENTION_DAYS"));
    }

    /**
     * Загружает properties из файла
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Unable to find application.properties, using defaults");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading application.properties: " + e.getMessage());
        }
    }

    /**
     * Получает значение свойства с возможностью переопределения через env
     * Приоритет: System.getenv() > application.properties
     */
    private String getProperty(String propertyKey, String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return properties.getProperty(propertyKey, getDefault(propertyKey));
    }

    /**
     * Возвращает дефолтные значения
     */
    private String getDefault(String key) {
        return switch (key) {
            case "db.url" -> "jdbc:postgresql://localhost:5432/fileservice";
            case "db.user" -> "postgres";
            case "db.password" -> "postgres";
            case "server.port" -> "8080";
            case "jwt.secret" -> "your-secret-key-change-this-in-production";
            case "jwt.expiration.ms" -> "86400000"; // 24 hours
            case "file.storage.path" -> "./uploads";
            case "file.retention.days" -> "30";
            default -> "";
        };
    }
}

