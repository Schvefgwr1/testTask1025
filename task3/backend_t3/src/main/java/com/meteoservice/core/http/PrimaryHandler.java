package com.meteoservice.core.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Базовый класс для всех HTTP handlers
 * Предоставляет общие методы для работы с запросами и ответами
 */
public abstract class PrimaryHandler {
    private final Gson gson;
    private final ResponseHelper responseHelper;

    protected PrimaryHandler(Gson gson, ResponseHelper responseHelper) {
        this.gson = gson;
        this.responseHelper = responseHelper;
    }

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        responseHelper.sendJson(exchange, statusCode, data);
    }

    protected <T> T parseRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        String requestBody = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));

        return gson.fromJson(requestBody, clazz);
    }

    protected String getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}

