package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.rotary.exchange.backend.model.CampDistrictStatus;
import org.rotary.exchange.backend.model.CampInstance;
import org.rotary.exchange.backend.model.CampStatus;

/**
 * Extended camp response that includes the local status for a specific district.
 * Used for coordinator views where they need to see their district's status.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Camp response with district-specific local status")
public class CampWithDistrictStatusDTO extends CampResponseDTO {
    
    @Schema(description = "Local status for the coordinator's district", example = "OPEN")
    private CampStatus localStatus;
    
    @Schema(description = "District ID this status applies to", example = "1")
    private Integer districtId;
    
    @Schema(description = "District code", example = "1820")
    private String districtCode;

    public CampWithDistrictStatusDTO(CampInstance camp, CampDistrictStatus districtStatus) {
        super(camp);
        if (districtStatus != null) {
            this.localStatus = districtStatus.getLocalStatus();
            this.districtId = districtStatus.getDistrict().getId();
            this.districtCode = districtStatus.getDistrict().getCode();
        }
    }
    
    public CampWithDistrictStatusDTO(CampInstance camp, Integer districtId, String districtCode, CampStatus localStatus) {
        super(camp);
        this.districtId = districtId;
        this.districtCode = districtCode;
        this.localStatus = localStatus;
    }
}
