package com.meteoservice.client;

import java.io.IOException;
import java.util.Map;

/**
 * Интерфейс HTTP клиента для выполнения запросов к внешним API
 */
public interface IHttpClient {
    /**
     * Выполняет GET запрос без параметров
     * 
     * @param url URL для запроса
     * @return тело ответа
     * @throws IOException при ошибке запроса
     */
    String get(String url) throws IOException;

    /**
     * Выполняет GET запрос с query параметрами
     * 
     * @param url URL для запроса
     * @param queryParams query параметры
     * @return тело ответа
     * @throws IOException при ошибке запроса
     */
    String get(String url, Map<String, String> queryParams) throws IOException;
}

