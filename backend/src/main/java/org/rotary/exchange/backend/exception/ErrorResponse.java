package org.rotary.exchange.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Standard error response structure for all API errors.
 */
@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error message describing what went wrong", example = "Resource not found")
        String message,

        @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
        LocalDateTime timestamp
) {}
