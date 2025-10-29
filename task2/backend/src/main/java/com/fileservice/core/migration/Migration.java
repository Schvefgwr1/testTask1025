package com.fileservice.core.migration;

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
    private String version;        // Например: V1, V2, V3
    private String description;    // Описание миграции
    private String filename;       // Имя файла миграции
    private String checksum;       // MD5 checksum для проверки целостности
    private Timestamp executedAt;  // Время выполнения
    private Boolean success;       // Успешно ли выполнена
}

