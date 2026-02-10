package com.fileservice.middleware;

import com.common.core.http.Middleware;
import com.common.core.http.MiddlewareChain;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Middleware для обработки CORS
 */
public class CorsMiddleware implements Middleware {

    @Override
    public void handle(HttpExchange exchange, MiddlewareChain next) throws IOException {
        // Добавляем CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        // Обработка preflight запросов
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        next.proceed(exchange);
    }
}

