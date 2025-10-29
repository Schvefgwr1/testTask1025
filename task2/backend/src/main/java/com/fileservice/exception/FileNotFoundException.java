package com.fileservice.exception;

import java.util.UUID;

/**
 * Файл не найден
 */
public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(UUID uuid) {
        super("File not found: " + uuid);
    }

    public FileNotFoundException(String message) {
        super(message);
    }
}

