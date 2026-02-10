package com.common.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

/**
 * Цепочка middleware
 */
public class MiddlewareChain {
    private final List<Middleware> middlewares;
    private final TerminalHandler terminalHandler;
    private int currentIndex = 0;

    public MiddlewareChain(List<Middleware> middlewares, TerminalHandler terminalHandler) {
        this.middlewares = middlewares;
        this.terminalHandler = terminalHandler;
    }

    public void proceed(HttpExchange exchange) throws IOException {
        if (currentIndex < middlewares.size()) {
            Middleware middleware = middlewares.get(currentIndex);
            currentIndex++;
            middleware.handle(exchange, this);
        } else {
            terminalHandler.handle(exchange);
        }
    }

    @FunctionalInterface
    public interface TerminalHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
