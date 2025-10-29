package com.fileservice.exception;

/**
 * Ошибка работы с базой данных - 500 Internal Server Error
 */
public class DatabaseException extends ApplicationException {
    public DatabaseException(String message) {
        super(message, 500);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, 500, cause);
    }
}

