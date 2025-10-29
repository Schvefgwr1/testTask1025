package com.fileservice.repository;

import com.fileservice.exception.DatabaseException;
import com.fileservice.mapper.FileInfoMapper;
import com.fileservice.mapper.IMapper;
import com.fileservice.model.FileInfo;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с файлами
 */
@AllArgsConstructor
public class FileRepository implements IFileRepository {
    private final Connection connection;
    private final IMapper<FileInfo> mapper;

    public FileInfo create(UUID uuid, Integer userId, String path) {
        String sql = "INSERT INTO files (uuid, user_id, path, created_at, download_count, isDeleted) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0, false) " +
                    "RETURNING uuid, user_id, path, created_at, last_download_at, download_count, isDeleted";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            stmt.setInt(2, userId);
            stmt.setString(3, path);
            
            ResultSet rs = stmt.executeQuery();
            FileInfo fileInfo = mapper.mapFromResultSetSafe(rs);
            
            if (fileInfo == null) {
                throw new DatabaseException("Failed to create file record");
            }
            
            return fileInfo;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create file record", e);
        }
    }

    public FileInfo findByUuid(UUID uuid) {
        String sql = "SELECT uuid, user_id, path, created_at, last_download_at, download_count, isDeleted " +
                    "FROM files WHERE uuid = ? AND isDeleted = false";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            
            return mapper.mapFromResultSetSafe(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find file by UUID", e);
        }
    }

    public List<FileInfo> findByUserId(Integer userId) {
        String sql = "SELECT uuid, user_id, path, created_at, last_download_at, download_count, isDeleted " +
                    "FROM files WHERE user_id = ? AND isDeleted = false ORDER BY created_at DESC";
        
        List<FileInfo> files = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                files.add(mapper.mapFromResultSet(rs));
            }
            
            return files;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find files by user ID", e);
        }
    }

    public void incrementDownloadCount(UUID uuid) {
        String sql = "UPDATE files SET download_count = download_count + 1, " +
                    "last_download_at = CURRENT_TIMESTAMP WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to increment download count", e);
        }
    }

    public List<FileInfo> findOldFiles(int daysOld) {
        String sql = "SELECT uuid, user_id, path, created_at, last_download_at, download_count, isDeleted " +
                    "FROM files WHERE created_at < CURRENT_TIMESTAMP - INTERVAL ? DAY " +
                    "AND isDeleted = false";
        
        List<FileInfo> files = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, daysOld);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                files.add(mapper.mapFromResultSet(rs));
            }
            
            return files;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find old files", e);
        }
    }

    public void markAsDeleted(UUID uuid) {
        String sql = "UPDATE files SET isDeleted = true WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark file as deleted", e);
        }
    }
}

