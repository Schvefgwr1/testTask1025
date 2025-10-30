package com.meteoservice.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация HTTP клиента на основе java.net.http.HttpClient (Java 11+)
 */
public class HttpClientImpl implements IHttpClient {
    private final HttpClient httpClient;

    public HttpClientImpl(int timeoutSeconds) {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(timeoutSeconds))
            .build();
    }

    @Override
    public String get(String url) throws IOException {
        return get(url, Map.of());
    }

    @Override
    public String get(String url, Map<String, String> queryParams) throws IOException {
        try {
            // Формируем URL с query параметрами
            String fullUrl = buildUrl(url, queryParams);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверяем статус код
            if (response.statusCode() != 200) {
                throw new IOException("HTTP request failed with status code: " + response.statusCode());
            }

            return response.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request interrupted", e);
        }
    }

    /**
     * Формирует полный URL с query параметрами
     */
    private String buildUrl(String baseUrl, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return baseUrl;
        }

        String queryString = queryParams.entrySet().stream()
            .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
            .collect(Collectors.joining("&"));

        return baseUrl + "?" + queryString;
    }

    /**
     * URL кодирует параметр
     */
    private String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

