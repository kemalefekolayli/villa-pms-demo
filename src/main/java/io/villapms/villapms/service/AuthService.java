package io.villapms.villapms.service;

import io.villapms.villapms.dto.LoginDto;
import io.villapms.villapms.model.User.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Simple in-memory store for refresh tokens (in production, use Redis or database)
    private final Map<String, Long> refreshTokenStore = new ConcurrentHashMap<>();

    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> authenticateUser(LoginDto loginDto) {
        UserAccount user = userService.findByEmail(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "tokenType", "Bearer",
                "expiresIn", 3600, // 1 hour
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "surname", user.getSurname()
                )
        );
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        Long userId = refreshTokenStore.get(refreshToken);
        if (userId == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserAccount user = userService.getUserById(userId);
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);

        // Remove old refresh token
        refreshTokenStore.remove(refreshToken);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken,
                "tokenType", "Bearer",
                "expiresIn", 3600
        );
    }

    private String generateAccessToken(UserAccount user) {
        // Simple token generation (in production, use proper JWT)
        String payload = user.getId() + ":" + user.getEmail() + ":" +
                LocalDateTime.now().plusHours(1).toEpochSecond(ZoneOffset.UTC);
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    private String generateRefreshToken(UserAccount user) {
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenStore.put(refreshToken, user.getId());
        return refreshToken;
    }
}