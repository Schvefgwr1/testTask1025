package com.fileservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FileStatsResponseDto {
    private final List<FileStatsDto> files;
    private final Integer totalFiles;

    @Data
    @Builder
    public static class FileStatsDto {
        private final String uuid;
        private final String fileName;
        private final String createdAt;
        private final String lastDownloadAt;
        private final Integer downloadCount;
        private final String downloadUrl;
    }
}