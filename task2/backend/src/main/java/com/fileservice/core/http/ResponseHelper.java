package com.fileservice.core.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Утилита для отправки HTTP ответов
 */
@AllArgsConstructor
public class ResponseHelper {
    private final Gson gson;

    /**
     * Отправляет JSON ответ (автоматически сериализует любой объект)
     */
    public void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String jsonResponse = gson.toJson(data);
        byte[] bytes = jsonResponse.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Отправляет бинарные данные (для файлов)
     */
    public void sendBinary(HttpExchange exchange, int statusCode, byte[] data, String contentType, String fileName) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Content-Disposition", 
            "attachment; filename=\"" + fileName + "\"");
        exchange.sendResponseHeaders(statusCode, data.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }
}

