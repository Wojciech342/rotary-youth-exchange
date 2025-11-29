package org.rotary.exchange.backend.message.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response containing new access token after refresh")
public class TokenRefreshResponse {
    @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Refresh token (same as provided or new if rotated)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
    
    @Schema(description = "Token type, always 'Bearer'", example = "Bearer")
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
