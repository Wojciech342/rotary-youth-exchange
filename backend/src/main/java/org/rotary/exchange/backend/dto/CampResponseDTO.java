package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.rotary.exchange.backend.model.CampInstance;
import org.rotary.exchange.backend.model.CampStatus;
import java.time.LocalDate;

@Data
@Schema(description = "Camp instance response with flattened template and coordinator data")
public class CampResponseDTO {
    // Instance IDs
    @Schema(description = "Unique camp instance ID", example = "1")
    private Integer id;
    
    @Schema(description = "Edition/year of this camp instance", example = "2024")
    private Integer edition;
    
    // Flattened Template Data (Easy for React to display)
    @Schema(description = "Camp name from template", example = "Summer Adventure Camp")
    private String name; 
    
    @Schema(description = "Camp description", example = "A week-long adventure camp...")
    private String description;
    
    @Schema(description = "URL to camp image", example = "/api/files/images/camp123.jpg")
    private String imageUrl;
    
    @Schema(description = "URL to camp flyer PDF", example = "/api/files/flyers/camp123.pdf")
    private String flyerPdfUrl;
    
    @Schema(description = "Minimum participant age", example = "15")
    private Integer ageMin;
    
    @Schema(description = "Maximum participant age", example = "18")
    private Integer ageMax;

    // Instance Data
    @Schema(description = "Camp start date", example = "2024-07-01")
    private LocalDate dateStart;
    
    @Schema(description = "Camp end date", example = "2024-07-14")
    private LocalDate dateEnd;
    
    @Schema(description = "Camp price", example = "500")
    private Integer price;
    
    // Status & Limits
    @Schema(description = "Global registration status", example = "OPEN")
    private CampStatus globalStatus;
    
    @Schema(description = "Maximum total participants", example = "30")
    private Integer limitTotal;
    
    // Coordinator Info (Safe fields only!)
    @Schema(description = "Coordinator's full name", example = "John Smith")
    private String coordinatorName;
    
    @Schema(description = "Coordinator ID", example = "1")
    private Integer coordinatorId;

    // Constructor to map Entity -> DTO
    public CampResponseDTO(CampInstance camp) {
        this.id = camp.getId();
        this.edition = camp.getEdition();
        this.dateStart = camp.getDateStart();
        this.dateEnd = camp.getDateEnd();
        this.price = camp.getPrice();
        this.globalStatus = camp.getGlobalStatus();
        this.limitTotal = camp.getLimitTotal();

        // Safe extraction from Template
        if (camp.getCampTemplate() != null) {
            this.name = camp.getCampTemplate().getName();
            this.description = camp.getCampTemplate().getDescription();
            this.imageUrl = camp.getCampTemplate().getImageUrl();
            this.flyerPdfUrl = camp.getCampTemplate().getFlyerPdfUrl();
            this.ageMin = camp.getCampTemplate().getAgeMin();
            this.ageMax = camp.getCampTemplate().getAgeMax();
        }

        // Safe extraction from Coordinator
        if (camp.getCoordinator() != null) {
            this.coordinatorId = camp.getCoordinator().getId();
            this.coordinatorName = camp.getCoordinator().getFirstName() + " " + camp.getCoordinator().getLastName();
            // NOTICE: We do NOT map passwordHash here. Safe!
        }
    }
}