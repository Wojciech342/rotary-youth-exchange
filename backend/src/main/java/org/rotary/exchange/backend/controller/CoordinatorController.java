package org.rotary.exchange.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.CoordinatorDTO;
import org.rotary.exchange.backend.dto.CoordinatorUpdateRequest;
import org.rotary.exchange.backend.exception.ErrorResponse;
import org.rotary.exchange.backend.service.CoordinatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinators")
@RequiredArgsConstructor
@Tag(name = "Coordinators", description = "Coordinator profile management and administration")
public class CoordinatorController {

    private final CoordinatorService coordinatorService;

    @Operation(
            summary = "Get coordinator by ID",
            description = "Retrieves the public profile of a coordinator. Used on camp detail pages."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coordinator found"),
            @ApiResponse(responseCode = "404", description = "Coordinator not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CoordinatorDTO> getCoordinatorById(
            @Parameter(description = "Coordinator ID", required = true, example = "1")
            @PathVariable Integer id) {
        return ResponseEntity.ok(coordinatorService.getCoordinatorById(id));
    }

    @Operation(
            summary = "Update own profile",
            description = "Allows authenticated coordinators to update their own profile information.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PutMapping("/me")
    public ResponseEntity<CoordinatorDTO> updateMyProfile(
            Authentication authentication,
            @RequestBody CoordinatorUpdateRequest request) {
        return ResponseEntity.ok(coordinatorService.updateProfile(authentication, request));
    }

    @Operation(
            summary = "Change own password",
            description = "Allows authenticated coordinators to change their password.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid old password or weak new password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @RequestBody PasswordChangeRequest request) {
        coordinatorService.changePassword(authentication, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all coordinators",
            description = "Retrieves a list of all coordinators in the system. Requires ADMIN or COORDINATOR role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coordinators retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN or COORDINATOR role")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<List<CoordinatorDTO>> getAllCoordinators() {
        return ResponseEntity.ok(coordinatorService.getAllCoordinators());
    }

    @Operation(
            summary = "Update any coordinator",
            description = "Allows admins to update any coordinator's profile information.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coordinator updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Coordinator not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoordinatorDTO> updateCoordinator(
            @Parameter(description = "Coordinator ID", required = true, example = "1")
            @PathVariable Integer id,
            @RequestBody CoordinatorUpdateRequest request) {
        return ResponseEntity.ok(coordinatorService.updateCoordinatorById(id, request));
    }

    @Operation(
            summary = "Assign district to coordinator",
            description = "Assigns a Rotary district to a coordinator. Admin only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "District assigned successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Coordinator or district not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/district/{districtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoordinatorDTO> assignDistrict(
            @Parameter(description = "Coordinator ID", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "District ID to assign", required = true, example = "1")
            @PathVariable Integer districtId) {
        return ResponseEntity.ok(coordinatorService.assignDistrict(id, districtId));
    }

    @Operation(
            summary = "Delete coordinator",
            description = "Permanently deletes a coordinator from the system. Admin only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Coordinator deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Coordinator not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoordinator(
            @Parameter(description = "Coordinator ID", required = true, example = "1")
            @PathVariable Integer id) {
        coordinatorService.deleteCoordinator(id);
        return ResponseEntity.noContent().build();
    }

    // Inner class for password change
    @Data
    @Schema(description = "Password change request")
    static class PasswordChangeRequest {
        @Schema(description = "Current password", requiredMode = Schema.RequiredMode.REQUIRED, example = "OldP@ssword123")
        private String oldPassword;
        
        @Schema(description = "New password (min 8 chars, must include uppercase, lowercase, number)", requiredMode = Schema.RequiredMode.REQUIRED, example = "NewP@ssword456")
        private String newPassword;
    }
}
