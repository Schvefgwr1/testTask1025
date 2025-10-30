package com.meteoservice.exception;

/**
 * Исключение для 404 Not Found (endpoint не найден)
 */
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}

