package com.common.core.migration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет файл миграции
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationFile {
    private String version;
    private String description;
    private String filename;
    private String content;
    private String checksum;
}
