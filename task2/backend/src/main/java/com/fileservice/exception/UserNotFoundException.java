package com.fileservice.exception;

import com.common.core.exception.NotFoundException;

/**
 * Пользователь не найден
 */
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String login) {
        super("User not found: " + login);
    }
}

