package com.fileservice.repository;

import com.fileservice.model.FileInfo;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с файлами
 */
public interface IFileRepository {
    /**
     * Создает запись о новом файле
     */
    FileInfo create(UUID uuid, Integer userId, String path);
    
    /**
     * Находит файл по UUID
     */
    FileInfo findByUuid(UUID uuid);
    
    /**
     * Находит все файлы пользователя
     */
    List<FileInfo> findByUserId(Integer userId);
    
    /**
     * Увеличивает счетчик скачиваний файла
     */
    void incrementDownloadCount(UUID uuid);
    
    /**
     * Находит старые файлы для удаления
     */
    List<FileInfo> findOldFiles(int daysOld);
    
    /**
     * Помечает файл как удаленный
     */
    void markAsDeleted(UUID uuid);
}

