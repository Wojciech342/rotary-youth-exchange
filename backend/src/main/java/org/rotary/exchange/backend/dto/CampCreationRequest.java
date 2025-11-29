package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request object for creating a new camp instance, optionally with a new template")
public class CampCreationRequest {
    // Template Data (Optional: if ID is null, create new)
    @Schema(description = "ID of existing template to use. If null, a new template will be created", example = "1")
    private Integer existingTemplateId;

    @Size(min = 3, max = 100, message = "Camp name must be between 3 and 100 characters")
    @Schema(description = "Name of the camp (required if creating new template)", example = "Summer Adventure Camp", minLength = 3, maxLength = 100)
    private String name;

    @Schema(description = "Detailed description of the camp", example = "A week-long adventure camp featuring hiking, camping, and outdoor activities")
    private String description;

    @Min(value = 1, message = "Minimum age must be at least 1")
    @Schema(description = "Minimum participant age", example = "15", minimum = "1")
    private Integer ageMin;

    @Min(value = 1, message = "Maximum age must be at least 1")
    @Schema(description = "Maximum participant age", example = "18", minimum = "1")
    private Integer ageMax;

    @Schema(description = "URL to camp image", example = "/api/files/images/camp123.jpg")
    private String imageUrl;

    // Instance Data
    @NotNull(message = "Coordinator ID is required")
    @Schema(description = "ID of the coordinator organizing this camp", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer coordinatorId;

    @NotNull(message = "Start date is required")
    @Schema(description = "Camp start date", example = "2024-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateStart;

    @NotNull(message = "End date is required")
    @Schema(description = "Camp end date", example = "2024-07-14", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateEnd;

    @Min(value = 0, message = "Price cannot be negative")
    @Schema(description = "Camp price in the smallest currency unit", example = "500", minimum = "0")
    private Integer price;

    @Schema(description = "Edition number of this camp (e.g., 2024 for annual camps)", example = "2024")
    private Integer edition;

    @Min(value = 1, message = "Total limit must be at least 1")
    @Schema(description = "Maximum total number of participants", example = "30", minimum = "1")
    private Integer limitTotal;

    @Min(value = 0, message = "Male limit cannot be negative")
    @Schema(description = "Maximum number of male participants", example = "15", minimum = "0")
    private Integer limitMale;

    @Min(value = 0, message = "Female limit cannot be negative")
    @Schema(description = "Maximum number of female participants", example = "15", minimum = "0")
    private Integer limitFemale;
}