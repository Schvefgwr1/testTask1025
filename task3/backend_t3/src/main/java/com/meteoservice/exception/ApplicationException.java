package com.meteoservice.exception;

/**
 * Базовое исключение приложения
 * Наследуется от RuntimeException для автоматического всплытия до exception handler middleware
 */
public abstract class ApplicationException extends RuntimeException {
    private final int statusCode;

    public ApplicationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApplicationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

