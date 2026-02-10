package com.common.core.exception;

/**
 * Исключение валидации данных - 400 Bad Request
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, 400);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, 400, cause);
    }
}
