package com.fileservice.service;

import com.fileservice.dto.AuthResponseDto;
import com.fileservice.dto.LoginResponseDto;
import com.fileservice.exception.InvalidCredentialsException;
import com.fileservice.exception.UserAlreadyExistsException;
import com.fileservice.exception.UserNotFoundException;
import com.fileservice.exception.ValidationException;
import com.fileservice.model.User;
import com.fileservice.repository.ISessionRepository;
import com.fileservice.repository.IUserRepository;
import com.fileservice.util.IPasswordHasher;
import com.fileservice.util.ITokenService;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сервис аутентификации и регистрации пользователей
 */
@AllArgsConstructor
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final ISessionRepository sessionRepository;
    private final ITokenService tokenService;
    private final IPasswordHasher passwordHasher;

    /**
     * Регистрирует нового пользователя
     */
    public AuthResponseDto register(String login, String password) {
        validateLoginAndPassword(login, password);
        if (login.length() > 64) {
            throw new ValidationException("Login must not exceed 64 characters");
        }

        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistsException(login);
        }

        String passwordHash = passwordHasher.hashPassword(password);
        User user = userRepository.create(login, passwordHash);

        return AuthResponseDto.builder()
                .userId(user.getId())
                .login(user.getLogin())
                .build();
    }

    /**
     * Аутентификация пользователя
     * @return DTO с токеном и информацией о пользователе
     */
    public LoginResponseDto login(String login, String password) {
        validateLoginAndPassword(login, password);

        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException(login);
        }

        if (!passwordHasher.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = createSessionForUser(user);
        return LoginResponseDto.builder()
                .token(token)
                .username(user.getLogin())
                .loginTime(LocalDateTime.now())
                .build();
    }

    private void validateLoginAndPassword(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }

    private String createSessionForUser(User user) {
        String token = tokenService.generateToken(user.getId(), user.getLogin());
        String tokenHash = passwordHasher.hashToken(token);
        sessionRepository.create(user.getId(), tokenHash);
        return token;
    }
}

