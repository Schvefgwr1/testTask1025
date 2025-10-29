package com.fileservice.core.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class PrimaryHandler {
    private final Gson gson;
    private final ResponseHelper responseHelper;
    private final MultipartParser multipartParser;

    protected PrimaryHandler(Gson gson, ResponseHelper responseHelper, MultipartParser multipartParser) {
        this.gson = gson;
        this.responseHelper = responseHelper;
        this.multipartParser = multipartParser;
    }

    protected void sendBinary(HttpExchange exchange, int statusCode, byte[] data, String contentType, String fileName) throws IOException {
        responseHelper.sendBinary(exchange, statusCode, data, contentType, fileName);
    }

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        responseHelper.sendJson(exchange, statusCode, data);
    }

    protected MultipartParser.FileUpload parseFileUpload(HttpExchange exchange) throws IOException {
        return multipartParser.parseFileUpload(exchange);
    }

    protected  <T> T parseRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        String requestBody = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));

        return gson.fromJson(requestBody, clazz);
    }
}
