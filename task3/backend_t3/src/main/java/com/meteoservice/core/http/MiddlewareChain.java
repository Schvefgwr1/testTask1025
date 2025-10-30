package com.meteoservice.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

/**
 * Цепочка middleware
 * Последовательно выполняет middleware и в конце вызывает финальный handler
 */
public class MiddlewareChain {
    private final List<Middleware> middlewares;
    private final TerminalHandler terminalHandler;
    private int currentIndex = 0;

    public MiddlewareChain(List<Middleware> middlewares, TerminalHandler terminalHandler) {
        this.middlewares = middlewares;
        this.terminalHandler = terminalHandler;
    }

    /**
     * Выполняет следующий middleware в цепочке
     */
    public void proceed(HttpExchange exchange) throws IOException {
        if (currentIndex < middlewares.size()) {
            Middleware middleware = middlewares.get(currentIndex);
            currentIndex++;
            middleware.handle(exchange, this);
        } else {
            terminalHandler.handle(exchange);
        }
    }

    /**
     * Финальный обработчик в конце цепочки
     */
    @FunctionalInterface
    public interface TerminalHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}

