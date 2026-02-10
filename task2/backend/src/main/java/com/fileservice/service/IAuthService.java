package com.fileservice.service;

import com.fileservice.dto.AuthResponseDto;
import com.common.core.transaction.Transactional;
import com.fileservice.dto.LoginResponseDto;

/**
 * Интерфейс сервиса аутентификации
 */
public interface IAuthService {
    /**
     * Регистрирует нового пользователя
     */
    @Transactional
    AuthResponseDto register(String login, String password);
    
    /**
     * Выполняет вход пользователя
     */
    @Transactional
    LoginResponseDto login(String login, String password);
}

