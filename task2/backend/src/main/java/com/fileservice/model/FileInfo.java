package com.fileservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private UUID uuid;
    private Integer userId;
    private String path;
    private Timestamp createdAt;
    private Timestamp lastDownloadAt;
    private Integer downloadCount;
    private Boolean isDeleted;
}

