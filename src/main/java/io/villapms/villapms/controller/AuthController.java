package io.villapms.villapms.controller;

import io.villapms.villapms.dto.LoginDto;
import io.villapms.villapms.dto.TokenRefreshDto;
import io.villapms.villapms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            Map<String, Object> authResponse = authService.authenticateUser(loginDto);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid credentials"
            ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody TokenRefreshDto tokenRefreshDto) {
        try {
            Map<String, Object> refreshResponse = authService.refreshToken(tokenRefreshDto.getRefreshToken());
            return ResponseEntity.ok(refreshResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid refresh token"
            ));
        }
    }
}