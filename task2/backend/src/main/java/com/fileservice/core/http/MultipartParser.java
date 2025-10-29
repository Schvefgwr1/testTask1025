package com.fileservice.core.http;

import com.fileservice.exception.ValidationException;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Парсер для multipart/form-data
 */
@AllArgsConstructor
public class MultipartParser {
    /**
     * DTO для загруженного файла
     */
    @Getter
    @AllArgsConstructor
    public static class FileUpload {
        private final String fileName;
        private final byte[] fileData;
    }

    /**
     * Парсит multipart/form-data запрос и извлекает файл
     */
    public FileUpload parseFileUpload(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new ValidationException("Content-Type must be multipart/form-data");
        }

        // Извлекаем boundary
        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new ValidationException("Invalid multipart request: boundary not found");
        }

        byte[] data = exchange.getRequestBody().readAllBytes();
        
        return parseMultipartData(data, boundary);
    }

    /**
     * Извлекает boundary из Content-Type header
     */
    private String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("boundary=")) {
                return trimmed.substring("boundary=".length());
            }
        }
        return null;
    }

    /**
     * Парсит multipart данные
     */
    private FileUpload parseMultipartData(byte[] data, String boundary) {
        // Конвертируем в строку для поиска boundaries
        String content = new String(data, StandardCharsets.ISO_8859_1); // ISO-8859-1 сохраняет binary данные
        String boundaryMarker = "--" + boundary;
        
        String[] parts = content.split(boundaryMarker);
        
        for (String part : parts) {
            if (part.contains("Content-Disposition") && part.contains("filename=")) {
                return parseFilePart(part);
            }
        }
        
        throw new ValidationException("No file found in multipart request");
    }

    /**
     * Парсит часть с файлом
     */
    private FileUpload parseFilePart(String part) {
        try {
            // Находим имя файла
            String fileName = extractFileName(part);
            if (fileName == null) {
                throw new ValidationException("Filename not found");
            }

            // Находим начало данных файла (после пустой строки)
            int headerEnd = part.indexOf("\r\n\r\n");
            if (headerEnd == -1) {
                headerEnd = part.indexOf("\n\n");
                if (headerEnd == -1) {
                    throw new ValidationException("Invalid multipart part format");
                }
                headerEnd += 2;
            } else {
                headerEnd += 4;
            }

            // Находим конец данных файла
            int dataEnd = part.lastIndexOf("\r\n");
            if (dataEnd == -1) {
                dataEnd = part.lastIndexOf("\n");
            }

            if (dataEnd <= headerEnd) {
                throw new ValidationException("Empty file data");
            }

            // Извлекаем данные файла
            String fileContentStr = part.substring(headerEnd, dataEnd);
            byte[] fileData = fileContentStr.getBytes(StandardCharsets.ISO_8859_1);

            return new FileUpload(fileName, fileData);

        } catch (Exception e) {
            throw new ValidationException("Failed to parse file upload: " + e.getMessage());
        }
    }

    /**
     * Извлекает имя файла из Content-Disposition header
     */
    private String extractFileName(String part) {
        int filenameIndex = part.indexOf("filename=\"");
        if (filenameIndex == -1) {
            return null;
        }
        
        int start = filenameIndex + 10; // длина "filename=\""
        int end = part.indexOf("\"", start);
        
        if (end == -1) {
            return null;
        }
        
        return part.substring(start, end);
    }
}

