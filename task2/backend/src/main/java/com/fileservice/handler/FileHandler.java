package com.fileservice.handler;

import com.common.core.http.PrimaryHandler;
import com.fileservice.dto.FileDownloadDto;
import com.fileservice.dto.FileStatsResponseDto;
import com.fileservice.dto.FileUploadResponseDto;
import com.fileservice.service.IFileService;
import com.common.core.http.MultipartParser;
import com.common.core.http.ResponseHelper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

/**
 * Handler для обработки запросов работы с файлами
 */
public class FileHandler extends PrimaryHandler {
    private final IFileService fileService;

    public FileHandler(IFileService fileService, Gson gson, ResponseHelper responseHelper, MultipartParser multipartParser) {
        super(gson, responseHelper, multipartParser);
        this.fileService = fileService;
    }

    /**
     * Обрабатывает загрузку файла
     */
    public void handleUpload(HttpExchange exchange) throws IOException {
        Integer userId = (Integer) exchange.getAttribute("userId");

        MultipartParser.FileUpload upload = parseFileUpload(exchange);

        FileUploadResponseDto response = fileService.uploadFile(
            userId, 
            upload.getFileName(), 
            upload.getFileData()
        );

        sendJsonResponse(exchange, 201, response);
    }

    /**
     * Обрабатывает скачивание файла
     */
    public void handleDownload(HttpExchange exchange, String uuidStr) throws IOException {
        UUID uuid = UUID.fromString(uuidStr);
        FileDownloadDto response = fileService.downloadFile(uuid);

        sendBinary(
            exchange, 
            200, 
            response.getData(), 
            response.getMimeType(), 
            response.getFileName()
        );
    }

    /**
     * Обрабатывает запрос статистики
     */
    public void handleStats(HttpExchange exchange) throws IOException {
        Integer userId = (Integer) exchange.getAttribute("userId");
        
        FileStatsResponseDto response = fileService.getFileStats(userId);

        sendJsonResponse(exchange, 200, response);
    }
}

