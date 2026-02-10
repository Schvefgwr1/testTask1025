package com.fileservice.exception;

import com.common.core.exception.ApplicationException;

/**
 * Исключение конфликта (например, пользователь уже существует) - 409 Conflict
 */
public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, 409, cause);
    }
}

