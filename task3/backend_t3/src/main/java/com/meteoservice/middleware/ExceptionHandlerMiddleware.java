package com.meteoservice.middleware;

import com.meteoservice.core.http.Middleware;
import com.meteoservice.core.http.MiddlewareChain;
import com.meteoservice.core.http.ResponseHelper;
import com.meteoservice.exception.ApplicationException;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Middleware для обработки исключений
 */
@AllArgsConstructor
public class ExceptionHandlerMiddleware implements Middleware {
    private final ResponseHelper responseHelper;

    @Override
    public void handle(HttpExchange exchange, MiddlewareChain next) throws IOException {
        try {
            next.proceed(exchange);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    /**
     * Обрабатывает исключение и отправляет JSON ответ с ошибкой
     */
    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        int statusCode;

        if (e instanceof ApplicationException) {
            ApplicationException appException = (ApplicationException) e;
            statusCode = appException.getStatusCode();
            errorResponse.put("error", appException.getMessage());
            errorResponse.put("statusCode", statusCode);

            if (statusCode >= 500) {
                System.err.println("Server error [" + statusCode + "]: " + e.getMessage());
                e.printStackTrace();
            } else {
                System.err.println("Client error [" + statusCode + "]: " + e.getMessage());
            }
        } else if (e instanceof IllegalArgumentException) {
            statusCode = 400;
            errorResponse.put("error", "Bad request: " + e.getMessage());
            errorResponse.put("statusCode", statusCode);
            System.err.println("Validation error: " + e.getMessage());
        } else {
            statusCode = 500;
            errorResponse.put("error", "Internal server error");
            errorResponse.put("statusCode", statusCode);
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        responseHelper.sendJson(exchange, statusCode, errorResponse);
    }
}

