package com.common.core.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Универсальная конфигурация приложения.
 * Читает настройки из application.properties с возможностью переопределения через переменные окружения.
 * Поддерживает свойства для: HTTP, DB, Redis, JWT, File, API.
 */
@Getter
public class Config {
    private final Properties properties;

    // HTTP
    private final int serverPort;

    // Database (PostgreSQL)
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    // Redis
    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;

    // JWT
    private final String jwtSecret;
    private final long jwtExpirationMs;

    // File storage
    private final String fileStoragePath;
    private final int fileRetentionDays;

    // API (для внешних сервисов)
    private final String geocodingApiUrl;
    private final String weatherApiUrl;
    private final int apiTimeoutSeconds;

    // Cache
    private final int cacheTimeoutSeconds;

    public Config() {
        this.properties = new Properties();
        loadProperties();

        this.serverPort = Integer.parseInt(getProperty("server.port", "SERVER_PORT"));
        this.dbUrl = getProperty("db.url", "DB_URL");
        this.dbUser = getProperty("db.user", "DB_USER");
        this.dbPassword = getProperty("db.password", "DB_PASSWORD");
        this.redisHost = getProperty("redis.host", "REDIS_HOST");
        this.redisPort = Integer.parseInt(getProperty("redis.port", "REDIS_PORT"));
        this.redisPassword = getProperty("redis.password", "REDIS_PASSWORD");
        this.jwtSecret = getProperty("jwt.secret", "JWT_SECRET");
        this.jwtExpirationMs = Long.parseLong(getProperty("jwt.expiration.ms", "JWT_EXPIRATION_MS"));
        this.fileStoragePath = getProperty("file.storage.path", "FILE_STORAGE_PATH");
        this.fileRetentionDays = Integer.parseInt(getProperty("file.retention.days", "FILE_RETENTION_DAYS"));
        this.geocodingApiUrl = getProperty("api.geocoding.url", "API_GEOCODING_URL");
        this.weatherApiUrl = getProperty("api.weather.url", "API_WEATHER_URL");
        this.apiTimeoutSeconds = Integer.parseInt(getProperty("api.timeout.seconds", "API_TIMEOUT_SECONDS"));
        this.cacheTimeoutSeconds = Integer.parseInt(getProperty("cache.timeout.seconds", "CACHE_TIMEOUT_SECONDS"));
    }

    /**
     * Получает произвольное свойство (для расширения)
     */
    public String getProperty(String propertyKey, String envKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return properties.getProperty(propertyKey, defaultValue);
    }

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

    private String getProperty(String propertyKey, String envKey) {
        return getProperty(propertyKey, envKey, getDefault(propertyKey));
    }

    private String getDefault(String key) {
        return switch (key) {
            case "server.port" -> "8080";
            case "db.url" -> "jdbc:postgresql://localhost:5432/fileservice";
            case "db.user" -> "postgres";
            case "db.password" -> "postgres";
            case "redis.host" -> "localhost";
            case "redis.port" -> "6379";
            case "redis.password" -> "";
            case "jwt.secret" -> "your-secret-key-change-this-in-production-min-32-chars";
            case "jwt.expiration.ms" -> "86400000";
            case "file.storage.path" -> "./uploads";
            case "file.retention.days" -> "30";
            case "api.geocoding.url" -> "https://geocoding-api.open-meteo.com/v1/search";
            case "api.weather.url" -> "https://api.open-meteo.com/v1/forecast";
            case "api.timeout.seconds" -> "10";
            case "cache.timeout.seconds" -> "172800";
            default -> "";
        };
    }
}
