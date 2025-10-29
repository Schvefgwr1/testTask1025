package com.fileservice.service;

import com.fileservice.core.config.Config;
import com.fileservice.model.FileInfo;
import com.fileservice.repository.IFileRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Сервис автоматической очистки старых файлов
 */
public class FileCleanupService {
    private final IFileRepository fileRepository;
    private final Config config;
    private Timer timer;

    public FileCleanupService(IFileRepository fileRepository, Config config) {
        this.fileRepository = fileRepository;
        this.config = config;
    }

    /**
     * Запускает периодическую задачу очистки файлов
     */
    public void start() {
        timer = new Timer("FileCleanupService", true);

        long delay = TimeUnit.HOURS.toMillis(1);  // Первый запуск через 1 час
        long period = TimeUnit.DAYS.toMillis(1);  // Повторять каждые 24 часа
        
        timer.scheduleAtFixedRate(new CleanupTask(), delay, period);
        System.out.println("File cleanup service started (runs daily)");
    }

    /**
     * Останавливает задачу очистки
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            System.out.println("File cleanup service stopped");
        }
    }

    private class CleanupTask extends TimerTask {
        @Override
        public void run() {
            try {
                System.out.println("Starting file cleanup task...");
                
                // Получаем файлы старше X дней
                List<FileInfo> oldFiles = fileRepository.findOldFiles(config.getFileRetentionDays());
                
                int deletedCount = 0;
                for (FileInfo fileInfo : oldFiles) {
                    try {
                        // Удаляем физический файл
                        Path filePath = Paths.get(config.getFileStoragePath(), fileInfo.getPath());
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                        }
                        
                        // Помечаем файл как удаленный в БД
                        fileRepository.markAsDeleted(fileInfo.getUuid());
                        
                        deletedCount++;
                    } catch (Exception e) {
                        System.err.println("Error deleting file " + fileInfo.getUuid() + ": " + e.getMessage());
                    }
                }
                
                System.out.println("✓ File cleanup completed. Deleted " + deletedCount + " files.");
                
            } catch (Exception e) {
                System.err.println("Error in file cleanup task: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

