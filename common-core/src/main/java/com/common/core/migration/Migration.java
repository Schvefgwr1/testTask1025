package com.common.core.migration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Модель миграции базы данных
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Migration {
    private Integer id;
    private String version;
    private String description;
    private String filename;
    private String checksum;
    private Timestamp executedAt;
    private Boolean success;
}
