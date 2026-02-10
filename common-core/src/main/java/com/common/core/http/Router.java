package com.common.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Интерфейс роутера для обработки HTTP запросов
 */
public interface Router {
    void route(HttpExchange exchange) throws IOException;
}
