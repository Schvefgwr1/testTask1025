package com.common.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Базовый интерфейс для middleware
 */
@FunctionalInterface
public interface Middleware {
    void handle(HttpExchange exchange, MiddlewareChain next) throws IOException;
}
