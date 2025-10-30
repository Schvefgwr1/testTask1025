package com.meteoservice.exception;

/**
 * Исключение при ошибке валидации входных данных
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, 400);
    }
}
