package com.fileservice.mapper;

import com.fileservice.exception.DatabaseException;
import com.fileservice.model.Session;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования ResultSet в модель Session
 */
public class SessionMapper implements IMapper<Session> {
    
    @Override
    public Session mapFromResultSet(ResultSet rs) throws SQLException {
        return Session.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .tokenHash(rs.getString("token_hash"))
            .startedAt(rs.getTimestamp("started_at"))
            .isActive(rs.getBoolean("is_active"))
            .build();
    }
    
    @Override
    public Session mapFromResultSetSafe(ResultSet rs) {
        try {
            if (rs == null || !rs.next()) {
                return null;
            }
            return mapFromResultSet(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Error mapping Session from ResultSet", e);
        }
    }
}

