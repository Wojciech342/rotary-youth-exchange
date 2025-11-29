package org.rotary.exchange.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.StatusUpdateDTO;
import org.rotary.exchange.backend.exception.AccessDeniedException;
import org.rotary.exchange.backend.exception.ErrorResponse;
import org.rotary.exchange.backend.model.CampDistrictStatus;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.rotary.exchange.backend.security.service.UserPrinciple;
import org.rotary.exchange.backend.service.DistrictStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "District Status", description = "Manage camp registration status per district")
public class DistrictStatusController {

    private final DistrictStatusService statusService;
    private final CoordinatorRepository coordinatorRepository;

    @Operation(
            summary = "Get camp status for a district",
            description = "Retrieves the registration status of a specific camp for a specific district"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Camp or district not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/camp/{campId}/district/{districtId}")
    public ResponseEntity<CampDistrictStatus> getStatus(
            @Parameter(description = "Camp instance ID", required = true, example = "1")
            @PathVariable Integer campId,
            @Parameter(description = "District ID", required = true, example = "1")
            @PathVariable Integer districtId) {
        return ResponseEntity.ok(statusService.getStatus(campId, districtId));
    }

    @Operation(
            summary = "Update camp status for a district",
            description = """
                    Updates the local registration status for a camp in a specific district.
                    
                    **Authorization:**
                    - Coordinators can only update status for their own assigned district
                    - Admins can update status for any district
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - can only update your own district's status"),
            @ApiResponse(responseCode = "404", description = "Camp or district not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/camp/{campId}/district/{districtId}")
    public ResponseEntity<CampDistrictStatus> updateLocalStatus(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Camp instance ID", required = true, example = "1")
            @PathVariable Integer campId,
            @Parameter(description = "District ID", required = true, example = "1")
            @PathVariable Integer districtId,
            @RequestBody StatusUpdateDTO dto) {
        
        // Check authorization: coordinator can only update their own district
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (!isAdmin) {
            Coordinator coordinator = coordinatorRepository.findById(user.getId())
                    .orElseThrow(() -> new AccessDeniedException("Coordinator not found"));
            
            if (coordinator.getDistrict() == null || !coordinator.getDistrict().getId().equals(districtId)) {
                throw new AccessDeniedException("You can only update status for your own district");
            }
        }
        
        return ResponseEntity.ok(statusService.updateLocalStatusManual(campId, districtId, dto.getStatus()));
    }
}