package com.fileservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Integer id;
    private Integer userId;
    private String tokenHash;
    private Timestamp startedAt;
    private Boolean isActive;
}

