package com.fileservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private String username;
    private LocalDateTime loginTime;
}
