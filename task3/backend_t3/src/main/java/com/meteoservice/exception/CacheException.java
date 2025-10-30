package com.meteoservice.exception;

/**
 * Исключение при ошибке работы с кэшем
 */
public class CacheException extends ApplicationException {
    public CacheException(String message) {
        super("Cache error: " + message, 500);
    }

    public CacheException(String message, Throwable cause) {
        super("Cache error: " + message, 500, cause);
    }
}

