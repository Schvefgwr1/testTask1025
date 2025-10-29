package com.fileservice.mapper;

import com.fileservice.exception.DatabaseException;
import com.fileservice.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования ResultSet в модель User
 */
public class UserMapper implements IMapper<User> {
    
    @Override
    public User mapFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getInt("id"))
            .login(rs.getString("login"))
            .passwordHash(rs.getString("password_hash"))
            .build();
    }
    
    @Override
    public User mapFromResultSetSafe(ResultSet rs) {
        try {
            if (rs == null || !rs.next()) {
                return null;
            }
            return mapFromResultSet(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Error mapping User from ResultSet", e);
        }
    }
}

