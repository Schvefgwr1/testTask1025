package com.common.core.http;

import com.common.core.exception.ValidationException;
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
    @Getter
    @AllArgsConstructor
    public static class FileUpload {
        private final String fileName;
        private final byte[] fileData;
    }

    public FileUpload parseFileUpload(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new ValidationException("Content-Type must be multipart/form-data");
        }

        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new ValidationException("Invalid multipart request: boundary not found");
        }

        byte[] data = exchange.getRequestBody().readAllBytes();
        return parseMultipartData(data, boundary);
    }

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

    private FileUpload parseMultipartData(byte[] data, String boundary) {
        String content = new String(data, StandardCharsets.ISO_8859_1);
        String boundaryMarker = "--" + boundary;
        String[] parts = content.split(boundaryMarker);

        for (String part : parts) {
            if (part.contains("Content-Disposition") && part.contains("filename=")) {
                return parseFilePart(part);
            }
        }

        throw new ValidationException("No file found in multipart request");
    }

    private FileUpload parseFilePart(String part) {
        try {
            String fileName = extractFileName(part);
            if (fileName == null) {
                throw new ValidationException("Filename not found");
            }

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

            int dataEnd = part.lastIndexOf("\r\n");
            if (dataEnd == -1) {
                dataEnd = part.lastIndexOf("\n");
            }

            if (dataEnd <= headerEnd) {
                throw new ValidationException("Empty file data");
            }

            String fileContentStr = part.substring(headerEnd, dataEnd);
            byte[] fileData = fileContentStr.getBytes(StandardCharsets.ISO_8859_1);

            return new FileUpload(fileName, fileData);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to parse file upload: " + e.getMessage());
        }
    }

    private String extractFileName(String part) {
        int filenameIndex = part.indexOf("filename=\"");
        if (filenameIndex == -1) {
            return null;
        }

        int start = filenameIndex + 10;
        int end = part.indexOf("\"", start);

        if (end == -1) {
            return null;
        }

        return part.substring(start, end);
    }
}
