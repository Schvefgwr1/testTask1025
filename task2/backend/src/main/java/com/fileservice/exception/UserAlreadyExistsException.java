package com.fileservice.exception;

/**
 * Пользователь с таким логином уже существует
 */
public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String login) {
        super("User with login '" + login + "' already exists");
    }
}

