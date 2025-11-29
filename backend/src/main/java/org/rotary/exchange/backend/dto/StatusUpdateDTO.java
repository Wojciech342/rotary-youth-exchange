package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.rotary.exchange.backend.model.CampStatus;

@Data
@Schema(description = "Request object for updating camp district status")
public class StatusUpdateDTO {
    @Schema(description = "New camp status", example = "OPEN", requiredMode = Schema.RequiredMode.REQUIRED)
    private CampStatus status;
}
