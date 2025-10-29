package com.fileservice.service;

import com.fileservice.core.config.Config;
import com.fileservice.dto.FileDownloadDto;
import com.fileservice.dto.FileStatsResponseDto;
import com.fileservice.dto.FileUploadResponseDto;
import com.fileservice.exception.FileNotFoundException;
import com.fileservice.exception.ValidationException;
import com.fileservice.model.FileInfo;
import com.fileservice.repository.IFileRepository;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с файлами
 */
@AllArgsConstructor
public class FileService implements IFileService {
    private final IFileRepository fileRepository;
    private final Config config;

    /**
     * Сохраняет файл на диск и создает запись в БД
     * @return DTO с информацией о загруженном файле
     */
    public FileUploadResponseDto uploadFile(Integer userId, String originalFileName, byte[] fileData) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new ValidationException("File name is required");
        }
        if (fileData == null || fileData.length == 0) {
            throw new ValidationException("File data is empty");
        }

        try {
            // Создаем директорию для хранения файлов
            Path uploadDir = Paths.get(config.getFileStoragePath());
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            UUID fileUuid = UUID.randomUUID();
            Path filePath = uploadDir.resolve(originalFileName);

            Files.write(filePath, fileData);

            FileInfo fileInfo = fileRepository.create(fileUuid, userId, originalFileName);

            return FileUploadResponseDto.builder()
                    .uuid(fileInfo.getUuid().toString())
                    .downloadUrl("/api/files/" + fileInfo.getUuid().toString())
                    .originalName(originalFileName)
                    .message("File uploaded successfully")
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    /**
     * Получает файл для скачивания
     * @return DTO с данными файла
     */
    public FileDownloadDto downloadFile(UUID uuid) {
        FileInfo fileInfo = fileRepository.findByUuid(uuid);
        if (fileInfo == null) {
            throw new FileNotFoundException(uuid);
        }

        try {
            Path filePath = Paths.get(config.getFileStoragePath(), fileInfo.getPath());
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found on disk");
            }

            byte[] fileData = Files.readAllBytes(filePath);

            fileRepository.incrementDownloadCount(uuid);

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return FileDownloadDto.builder()
                    .data(fileData)
                    .mimeType(mimeType)
                    .fileName(fileInfo.getPath())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    /**
     * Получает статистику файлов пользователя
     */
    public FileStatsResponseDto getFileStats(Integer userId) {
        List<FileInfo> files = fileRepository.findByUserId(userId);
        if (files.isEmpty()) {
            return FileStatsResponseDto.builder().totalFiles(0).build();
        }

        List<FileStatsResponseDto.FileStatsDto> fileStats = files.stream()
            .map(file -> FileStatsResponseDto.FileStatsDto.builder()
                    .uuid(file.getUuid().toString())
                    .fileName(file.getPath())
                    .createdAt(file.getCreatedAt().toString())
                    .lastDownloadAt(file.getLastDownloadAt() != null ? file.getLastDownloadAt().toString() : null)
                    .downloadCount(file.getDownloadCount())
                    .downloadUrl("/api/files/" + file.getUuid().toString())
                    .build()
            )
            .collect(Collectors.toList());

        return FileStatsResponseDto.builder()
                .files(fileStats)
                .totalFiles(files.size())
                .build();
    }
}

