package com.fileservice.exception;

/**
 * Исключение для случаев, когда ресурс не найден
 * HTTP статус: 404
 */
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, 404, cause);
    }
}
