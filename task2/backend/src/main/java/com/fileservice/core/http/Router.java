package com.fileservice.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Интерфейс роутера для обработки HTTP запросов
 * 
 * Определяет контракт для маршрутизации запросов к соответствующим обработчикам
 */
public interface Router {
    /**
     * Обрабатывает HTTP запрос и направляет его к нужному handler'у
     * 
     * @param exchange HTTP запрос
     * @throws IOException если произошла ошибка при обработке запроса
     */
    void route(HttpExchange exchange) throws IOException;
}

