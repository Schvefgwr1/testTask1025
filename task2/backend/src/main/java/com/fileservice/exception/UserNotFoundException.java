package com.fileservice.exception;

/**
 * Пользователь не найден
 */
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String login) {
        super("User not found: " + login);
    }
}

