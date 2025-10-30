package com.meteoservice.exception;

/**
 * Исключение при ошибке обращения к Weather API
 */
public class WeatherApiException extends ApplicationException {
    public WeatherApiException(String message) {
        super("Weather API error: " + message, 502);
    }

    public WeatherApiException(String message, Throwable cause) {
        super("Weather API error: " + message, 502, cause);
    }
}

