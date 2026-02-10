package com.fileservice.service;

import com.fileservice.dto.FileDownloadDto;
import com.fileservice.dto.FileStatsResponseDto;
import com.fileservice.dto.FileUploadResponseDto;
import com.common.core.transaction.Transactional;

import java.util.UUID;

/**
 * Интерфейс сервиса для работы с файлами
 */
public interface IFileService {
    /**
     * Загружает файл на сервер
     */
    @Transactional
    FileUploadResponseDto uploadFile(Integer userId, String originalFileName, byte[] fileData);
    
    /**
     * Скачивает файл с сервера
     */
    @Transactional
    FileDownloadDto downloadFile(UUID uuid);
    
    /**
     * Получает статистику файлов пользователя
     */
    FileStatsResponseDto getFileStats(Integer userId);
}

