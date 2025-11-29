package org.rotary.exchange.backend.repository;

import io.swagger.v3.oas.annotations.media.Schema;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    @Schema(description = "Revoke all refresh tokens for a coordinator (used on logout-all, password change)")
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.coordinator = :coordinator AND rt.revoked = false")
    int revokeAllByCoordinator(@Param("coordinator") Coordinator coordinator);

    @Schema(description = "Revoke a specific token (used on single logout)")
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    int revokeByToken(@Param("token") String token);

    @Schema(description = "Delete expired tokens (for cleanup job)")
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now OR rt.revoked = true")
    int deleteExpiredAndRevoked(@Param("now") Instant now);
}
