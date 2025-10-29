package com.fileservice.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для ответа при аутентификации
 */
@Data
@Builder
public class AuthResponseDto {
    private final Integer userId;
    private final String login;
}
