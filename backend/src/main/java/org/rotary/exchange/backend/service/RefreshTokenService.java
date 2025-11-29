package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rotary.exchange.backend.exception.TokenRefreshException;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.model.RefreshToken;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.rotary.exchange.backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration:604800}")  // 7 days default
    private long refreshTokenDurationSeconds;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CoordinatorRepository coordinatorRepository;

    /**
     * Create a new refresh token for a coordinator
     */
    @Transactional
    public RefreshToken createRefreshToken(Integer coordinatorId) {
        Coordinator coordinator = coordinatorRepository.findById(coordinatorId)
                .orElseThrow(() -> new RuntimeException("Coordinator not found"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCoordinator(coordinator);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSeconds));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validate and return the refresh token
     */
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new TokenRefreshException("Refresh token has been revoked. Please login again.");
        }

        if (refreshToken.isExpired()) {
            throw new TokenRefreshException("Refresh token has expired. Please login again.");
        }

        return refreshToken;
    }

    /**
     * Revoke a specific refresh token (single device logout)
     */
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeByToken(token);
    }

    /**
     * Revoke all refresh tokens for a coordinator (logout from all devices)
     */
    @Transactional
    public void revokeAllTokensForCoordinator(Coordinator coordinator) {
        int revokedCount = refreshTokenRepository.revokeAllByCoordinator(coordinator);
        log.info("Revoked {} refresh tokens for coordinator {}", revokedCount, coordinator.getEmail());
    }

    /**
     * Cleanup job - runs daily at 3 AM to remove expired/revoked tokens
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = refreshTokenRepository.deleteExpiredAndRevoked(Instant.now());
        log.info("Cleaned up {} expired/revoked refresh tokens", deletedCount);
    }
}
