package com.fileservice.repository;

import com.fileservice.exception.DatabaseException;
import com.fileservice.mapper.IMapper;
import com.fileservice.mapper.SessionMapper;
import com.fileservice.model.Session;
import lombok.AllArgsConstructor;

import java.sql.*;

/**
 * Репозиторий для работы с сессиями
 */
@AllArgsConstructor
public class SessionRepository implements ISessionRepository {
    private final Connection connection;
    private final IMapper<Session> mapper;

    public Session create(Integer userId, String tokenHash) {
        String sql = "INSERT INTO sessions (user_id, token_hash, started_at, is_active) " +
                    "VALUES (?, ?, CURRENT_TIMESTAMP, true) " +
                    "RETURNING id, user_id, token_hash, started_at, is_active";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, tokenHash);
            
            ResultSet rs = stmt.executeQuery();
            Session session = mapper.mapFromResultSetSafe(rs);
            
            if (session == null) {
                throw new DatabaseException("Failed to create session");
            }
            
            return session;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create session", e);
        }
    }

    public Session findByTokenHash(String tokenHash) {
        String sql = "SELECT id, user_id, token_hash, started_at, is_active " +
                    "FROM sessions WHERE token_hash = ? AND is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            ResultSet rs = stmt.executeQuery();
            
            return mapper.mapFromResultSetSafe(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find session by token hash", e);
        }
    }

    public void deactivateSession(String tokenHash) {
        String sql = "UPDATE sessions SET is_active = false WHERE token_hash = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deactivate session", e);
        }
    }
}

