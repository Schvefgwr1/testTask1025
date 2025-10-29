package com.fileservice.exception;

/**
 * Неверные учетные данные
 */
public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("Invalid login or password");
    }
}

