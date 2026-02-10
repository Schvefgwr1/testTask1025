package com.fileservice.repository;

import com.common.core.exception.DatabaseException;
import com.fileservice.mapper.IMapper;
import com.fileservice.model.User;
import lombok.AllArgsConstructor;

import java.sql.*;

/**
 * Репозиторий для работы с пользователями
 */
@AllArgsConstructor
public class UserRepository implements IUserRepository {
    private final Connection connection;
    private final IMapper<User> mapper;

    public User findByLogin(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            
            return mapper.mapFromResultSetSafe(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by login", e);
        }
    }

    public User create(String login, String passwordHash) {
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id, login, password_hash";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, passwordHash);
            
            ResultSet rs = stmt.executeQuery();
            User user = mapper.mapFromResultSetSafe(rs);
            
            if (user == null) {
                throw new DatabaseException("Failed to create user");
            }
            
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create user", e);
        }
    }

    public boolean existsByLogin(String login) {
        return findByLogin(login) != null;
    }
}

