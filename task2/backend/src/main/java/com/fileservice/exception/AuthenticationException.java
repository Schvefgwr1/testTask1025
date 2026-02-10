package com.fileservice.exception;

import com.common.core.exception.ApplicationException;

/**
 * Исключение аутентификации - 401 Unauthorized
 */
public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message, 401);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}

