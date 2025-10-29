package com.fileservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDto {
    private final String uuid;
    private final String downloadUrl;
    private final String originalName;
    private final String message;
}
