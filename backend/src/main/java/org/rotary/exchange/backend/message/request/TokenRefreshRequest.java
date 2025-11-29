package org.rotary.exchange.backend.message.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for refreshing access token")
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "The refresh token obtained during login", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
