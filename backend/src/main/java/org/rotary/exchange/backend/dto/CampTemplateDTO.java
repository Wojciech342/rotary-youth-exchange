package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.rotary.exchange.backend.model.CampTemplate;

@Data
@Schema(description = "Camp template data transfer object")
public class CampTemplateDTO {
    @Schema(description = "Template ID", example = "1")
    private Integer id;
    
    @Schema(description = "Template name", example = "Summer Adventure Camp")
    private String name;
    
    @Schema(description = "Template description", example = "A week-long adventure camp...")
    private String description;
    
    @Schema(description = "Minimum participant age", example = "15")
    private Integer ageMin;
    
    @Schema(description = "Maximum participant age", example = "18")
    private Integer ageMax;
    
    @Schema(description = "URL to camp image", example = "/api/files/images/camp123.jpg")
    private String imageUrl;
    
    @Schema(description = "URL to camp flyer PDF", example = "/api/files/flyers/camp123.pdf")
    private String flyerPdfUrl;

    public CampTemplateDTO(CampTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.description = template.getDescription();
        this.ageMin = template.getAgeMin();
        this.ageMax = template.getAgeMax();
        this.imageUrl = template.getImageUrl();
        this.flyerPdfUrl = template.getFlyerPdfUrl();
    }
}
