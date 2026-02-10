package com.fileservice.handler;

import com.common.core.http.PrimaryHandler;
import com.fileservice.dto.AuthResponseDto;
import com.fileservice.dto.LoginRequest;
import com.fileservice.dto.LoginResponseDto;
import com.fileservice.dto.RegisterRequest;
import com.fileservice.service.IAuthService;
import com.common.core.http.MultipartParser;
import com.common.core.http.ResponseHelper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Handler для обработки запросов аутентификации
 */
public class AuthHandler extends PrimaryHandler {
    private final IAuthService authService;

    public AuthHandler(IAuthService authService, Gson gson, ResponseHelper responseHelper, MultipartParser multipartParser) {
        super(gson, responseHelper, multipartParser);
        this.authService = authService;
    }

    /**
     * Обрабатывает запрос регистрации
     */
    public void handleRegister(HttpExchange exchange) throws IOException {
        RegisterRequest request = parseRequestBody(exchange, RegisterRequest.class);
        
        AuthResponseDto response = authService.register(
            request.getLogin(),
            request.getPassword()
        );

        sendJsonResponse(exchange, 201, response);
    }

    /**
     * Обрабатывает запрос входа
     */
    public void handleLogin(HttpExchange exchange) throws IOException {
        LoginRequest request = super.parseRequestBody(exchange, LoginRequest.class);
        
        LoginResponseDto response = authService.login(
            request.getLogin(),
            request.getPassword()
        );

        sendJsonResponse(exchange, 200, response);
    }
}

