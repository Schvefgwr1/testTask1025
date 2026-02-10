package com.fileservice.mapper;

import com.common.core.exception.DatabaseException;
import com.fileservice.model.FileInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Маппер для преобразования ResultSet в модель FileInfo
 */
public class FileInfoMapper implements IMapper<FileInfo> {
    
    @Override
    public FileInfo mapFromResultSet(ResultSet rs) throws SQLException {
        Timestamp lastDownloadAt = rs.getTimestamp("last_download_at");
        if (rs.wasNull()) {
            lastDownloadAt = null;
        }
        
        return FileInfo.builder()
            .uuid((UUID) rs.getObject("uuid"))
            .userId(rs.getInt("user_id"))
            .path(rs.getString("path"))
            .createdAt(rs.getTimestamp("created_at"))
            .lastDownloadAt(lastDownloadAt)
            .downloadCount(rs.getInt("download_count"))
            .isDeleted(rs.getBoolean("isDeleted"))
            .build();
    }
    
    @Override
    public FileInfo mapFromResultSetSafe(ResultSet rs) {
        try {
            if (rs == null || !rs.next()) {
                return null;
            }
            return mapFromResultSet(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Error mapping FileInfo from ResultSet", e);
        }
    }
}

