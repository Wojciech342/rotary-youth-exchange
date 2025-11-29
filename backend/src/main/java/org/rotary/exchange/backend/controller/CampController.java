package org.rotary.exchange.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.*;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.CampStatus;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.model.District;
import org.rotary.exchange.backend.repository.DistrictRepository;
import org.rotary.exchange.backend.security.service.UserPrinciple;
import org.rotary.exchange.backend.service.CampManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
@Tag(name = "Camps", description = "Camp management operations")
public class CampController {

    private final CampManagementService campService;
    private final DistrictRepository districtRepository;

    @Operation(
            summary = "Search camps for a district",
            description = """
                    Search for camps available to students of a specific district.
                    
                    **Requires a valid district access code** in the `code` parameter.
                    Only shows camps with status AVAILABLE, ONLY_MALE, or ONLY_FEMALE for that district.
                    
                    Students receive a link with this code from their Rotary district coordinator.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of camps available for the district"),
            @ApiResponse(responseCode = "404", description = "Invalid access code - district not found")
    })
    @GetMapping
    public ResponseEntity<Page<CampResponseDTO>> getPublicCamps(
            @Parameter(description = "District access code (required)", required = true, example = "a7f3x9k2z1")
            @RequestParam String code,
            @Parameter(description = "Additional search filters")
            CampSearchCriteria criteria,
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 12, sort = "dateStart") Pageable pageable) {
        
        // Validate and resolve the district from access code
        District district = districtRepository.findByAccessCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("District", "accessCode", code));
        
        // Set the district ID in criteria for filtering
        criteria.setDistrictId(district.getId());
        
        return ResponseEntity.ok(campService.getPublicCamps(criteria, pageable));
    }

    @Operation(
            summary = "Get camp details",
            description = """
                    Get detailed information about a specific camp.
                    
                    **Requires a valid district access code** to ensure the student has permission to view this camp.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camp details",
                    content = @Content(schema = @Schema(implementation = CampResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Camp not found or invalid access code")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CampResponseDTO> getCampDetails(
            @Parameter(description = "District access code (required)", required = true, example = "a7f3x9k2z1")
            @RequestParam String code,
            @Parameter(description = "Camp ID", required = true)
            @PathVariable Integer id) {
        
        // Validate the access code exists
        District district = districtRepository.findByAccessCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("District", "accessCode", code));
        
        // Get camp details (could add additional check that camp is available for this district)
        return ResponseEntity.ok(campService.getCampById(id, district.getId()));
    }

    @Operation(
            summary = "Get my camps",
            description = "Get camps created by the authenticated coordinator. Supports filtering and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of coordinator's camps"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-camps")
    public ResponseEntity<Page<CampResponseDTO>> getMyCamps(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Search criteria") CampSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "edition") Pageable pageable) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        criteria.setCoordinatorId(userPrinciple.getId());
        return ResponseEntity.ok(campService.getCoordinatorCamps(criteria, pageable));
    }

    @Operation(
            summary = "Get camps for my district",
            description = """
                    Get all camps available for the coordinator's assigned district.
                    
                    Each camp includes the local status for the coordinator's district.
                    Coordinators can only view and manage camps for their own assigned district.
                    
                    **Note:** ARCHIVED camps are excluded from the results.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of camps for the district with local statuses"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Coordinator has no assigned district")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/district")
    public ResponseEntity<Page<CampWithDistrictStatusDTO>> getDistrictCamps(
            @Parameter(hidden = true) Authentication authentication,
            @PageableDefault(size = 12, sort = "dateStart") Pageable pageable) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        
        // Get coordinator's district
        Coordinator coordinator = campService.getCoordinatorWithDistrict(userPrinciple.getId());
        if (coordinator.getDistrict() == null) {
            throw new ResourceNotFoundException("District", "coordinatorId", userPrinciple.getId());
        }
        
        District district = coordinator.getDistrict();
        return ResponseEntity.ok(campService.getCampsForDistrict(district.getId(), district.getCode(), pageable));
    }

    @Operation(
            summary = "Create new camp",
            description = """
                    Create a new camp instance. You can either:
                    - Provide an existingTemplateId to reuse an existing template
                    - Provide template data (name, description, etc.) to create a new template
                    
                    The coordinator ID is automatically set from the authentication token.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camp created successfully",
                    content = @Content(schema = @Schema(implementation = CampResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<CampResponseDTO> createCamp(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody CampCreationRequest request) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        request.setCoordinatorId(userPrinciple.getId());
        return ResponseEntity.ok(campService.createCamp(request));
    }

    @Operation(
            summary = "Update camp",
            description = "Update camp details. Only the camp owner or an admin can update."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camp updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the owner or admin"),
            @ApiResponse(responseCode = "404", description = "Camp not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<CampResponseDTO> updateCampDetails(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Camp ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody CampCreationRequest request) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(campService.updateCampDetails(id, user.getId(), isAdmin, request));
    }

    @Operation(
            summary = "Update camp status",
            description = """
                    Update the global status of a camp. Status changes cascade to district-level statuses.
                    
                    Available statuses: AVAILABLE, ONLY_MALE, ONLY_FEMALE, NOT_AVAILABLE, ARCHIVED
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the owner or admin"),
            @ApiResponse(responseCode = "404", description = "Camp not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CampResponseDTO> updateGlobalStatus(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Camp ID", required = true) @PathVariable Integer id,
            @RequestBody StatusUpdateDTO dto) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(campService.updateGlobalStatus(id, user.getId(), isAdmin, dto.getStatus()));
    }

    @Operation(
            summary = "Archive camp",
            description = "Soft-delete a camp by setting its status to ARCHIVED. Only the owner or admin can archive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Camp archived successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the owner or admin"),
            @ApiResponse(responseCode = "404", description = "Camp not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveCamp(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Camp ID", required = true) @PathVariable Integer id) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        campService.archiveCamp(id, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all camp statuses",
            description = "Retrieves all possible camp status values (e.g., DRAFT, OPEN, CLOSED, CANCELLED)"
    )
    @ApiResponse(responseCode = "200", description = "Statuses retrieved successfully")
    @GetMapping("/statuses")
    public List<CampStatus> getCampStatuses() {
        return Arrays.asList(CampStatus.values());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}