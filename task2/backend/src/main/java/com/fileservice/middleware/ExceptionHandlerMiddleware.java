package com.fileservice.middleware;

import com.fileservice.core.http.Middleware;
import com.fileservice.core.http.MiddlewareChain;
import com.fileservice.exception.ApplicationException;
import com.fileservice.core.http.ResponseHelper;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Middleware для обработки исключений
 * Оборачивает всю цепочку в try-catch
 */
@AllArgsConstructor
public class ExceptionHandlerMiddleware implements Middleware {
    private final ResponseHelper responseHelper;

    @Override
    public void handle(HttpExchange exchange, MiddlewareChain next) throws IOException {
        try {
            // Продолжаем цепочку
            next.proceed(exchange);
        } catch (Exception e) {
            // Ловим любые исключения и обрабатываем
            handleException(exchange, e);
        }
    }

    /**
     * Обрабатывает исключение и отправляет JSON ответ
     */
    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        int statusCode;

        if (e instanceof ApplicationException) {
            // Наши кастомные исключения
            ApplicationException appException = (ApplicationException) e;
            statusCode = appException.getStatusCode();
            errorResponse.put("error", appException.getMessage());
            
            // Логируем только серверные ошибки (5xx)
            if (statusCode >= 500) {
                System.err.println("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (e instanceof IllegalArgumentException) {
            statusCode = 400;
            errorResponse.put("error", e.getMessage());
        } else {
            // Неожиданные исключения
            statusCode = 500;
            errorResponse.put("error", "Internal server error");
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        responseHelper.sendJson(exchange, statusCode, errorResponse);
    }
}

