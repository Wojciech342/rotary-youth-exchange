package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Schema(description = "Search criteria for filtering camps")
public class CampSearchCriteria {
    @Schema(description = "Text search query (searches name and description)", example = "adventure")
    private String query;
    
    @Schema(description = "Filter by participant age (returns camps where age is within range)", example = "16")
    private Integer age;
    
    @Schema(description = "Maximum price filter", example = "1000")
    private Integer maxPrice;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Filter camps starting on or after this date", example = "2024-06-01")
    private LocalDate dateFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Filter camps ending on or before this date", example = "2024-08-31")
    private LocalDate dateTo;
    
    @Schema(description = "Filter by coordinator ID (for authenticated coordinator queries)", example = "1", hidden = true)
    private Integer coordinatorId;
    
    @Schema(description = "District ID resolved from access code (internal use)", hidden = true)
    private Integer districtId;
}