package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for updating coordinator profile")
public class CoordinatorUpdateRequest {
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Smith")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1-555-123-4567")
    private String phone;
    
    @Schema(description = "URL to profile picture", example = "/api/files/images/profile123.jpg")
    private String profilePictureUrl;
    
    @Schema(description = "Coordinator bio/description", example = "Youth exchange coordinator since 2015...")
    private String description;
}
