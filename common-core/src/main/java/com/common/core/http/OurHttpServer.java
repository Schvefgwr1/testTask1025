package com.common.core.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Универсальный HTTP сервер
 */
public class OurHttpServer {
    private final HttpServer server;
    private final Router router;

    public OurHttpServer(int port, Router router) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.router = router;

        server.createContext("/", new RequestHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            router.route(exchange);
        }
    }
}
