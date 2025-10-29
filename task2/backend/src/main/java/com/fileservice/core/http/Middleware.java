package com.fileservice.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Базовый интерфейс для middleware
 * Реализует паттерн Chain of Responsibility
 */
@FunctionalInterface
public interface Middleware {
    /**
     * Обрабатывает HTTP запрос
     * @param exchange HTTP обмен
     * @param next следующий middleware/handler в цепочке
     */
    void handle(HttpExchange exchange, MiddlewareChain next) throws IOException;
}

