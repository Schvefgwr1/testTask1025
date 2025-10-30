package com.meteoservice.core.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

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
}

