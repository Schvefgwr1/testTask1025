package com.fileservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDownloadDto {
    private final byte[] data;
    private final String mimeType;
    private final String fileName;

}