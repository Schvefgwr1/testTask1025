package com.common.core.exception;

/**
 * Исключение для ошибок работы с базой данных
 * HTTP статус: 500
 */
public class DatabaseException extends ApplicationException {
    public DatabaseException(String message) {
        super(message, 500);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, 500, cause);
    }
}
