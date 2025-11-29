package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for creating or updating a camp template")
public class CampTemplateRequest {
    @Schema(description = "Template name", example = "Summer Adventure Camp", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "Template description", example = "A week-long adventure camp featuring outdoor activities")
    private String description;
    
    @Schema(description = "Minimum participant age", example = "15")
    private Integer ageMin;
    
    @Schema(description = "Maximum participant age", example = "18")
    private Integer ageMax;
    
    @Schema(description = "URL to camp image", example = "/api/files/images/camp123.jpg")
    private String imageUrl;
    
    @Schema(description = "URL to camp flyer PDF", example = "/api/files/flyers/camp123.pdf")
    private String flyerPdfUrl;
}
