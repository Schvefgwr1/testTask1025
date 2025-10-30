package com.meteoservice.core.config;

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
    private final int serverPort;
    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;
    private final int cacheTimeoutSeconds;
    private final String geocodingApiUrl;
    private final String weatherApiUrl;
    private final int apiTimeoutSeconds;

    public Config() {
        this.properties = new Properties();
        loadProperties();
        
        // Загружаем значения с возможностью переопределения через env
        this.serverPort = Integer.parseInt(getProperty("server.port", "SERVER_PORT"));
        this.redisHost = getProperty("redis.host", "REDIS_HOST");
        this.redisPort = Integer.parseInt(getProperty("redis.port", "REDIS_PORT"));
        this.redisPassword = getProperty("redis.password", "REDIS_PASSWORD");
        this.cacheTimeoutSeconds = Integer.parseInt(getProperty("cache.timeout.seconds", "CACHE_TIMEOUT_SECONDS"));
        this.geocodingApiUrl = getProperty("api.geocoding.url", "API_GEOCODING_URL");
        this.weatherApiUrl = getProperty("api.weather.url", "API_WEATHER_URL");
        this.apiTimeoutSeconds = Integer.parseInt(getProperty("api.timeout.seconds", "API_TIMEOUT_SECONDS"));
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
            case "server.port" -> "8080";
            case "redis.host" -> "localhost";
            case "redis.port" -> "6379";
            case "redis.password" -> "";
            case "cache.timeout.seconds" -> "172800";
            case "api.geocoding.url" -> "https://geocoding-api.open-meteo.com/v1/search";
            case "api.weather.url" -> "https://api.open-meteo.com/v1/forecast";
            case "api.timeout.seconds" -> "10";
            default -> "";
        };
    }
}

