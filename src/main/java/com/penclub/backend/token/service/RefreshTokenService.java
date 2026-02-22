package com.penclub.backend.token.service;

import com.penclub.backend.exception.TokenRefreshException;
import com.penclub.backend.token.entity.RefreshToken;
import com.penclub.backend.token.repository.RefreshTokenRepository;
import com.penclub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Creates and persists a new refresh token for the given user.
     * Revokes all existing active tokens for the user before creating a new one.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoke all existing tokens for this user
        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Created refresh token for user: {}", user.getEmail());
        return saved;
    }

    /**
     * Finds a refresh token by its token string.
     * Throws TokenRefreshException if not found, expired, or revoked.
     */
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new TokenRefreshException(token, "Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new TokenRefreshException(token, "Refresh token has expired. Please login again");
        }

        return refreshToken;
    }

    /**
     * Revokes all refresh tokens for the given user (used during logout).
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
        log.debug("Revoked all refresh tokens for user: {}", user.getEmail());
    }

    /**
     * Deletes all refresh tokens for the given user.
     */
    @Transactional
    public void deleteAllUserTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user);
        log.debug("Deleted all refresh tokens for user: {}", user.getEmail());
    }
}
