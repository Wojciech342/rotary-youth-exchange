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
import org.rotary.exchange.backend.dto.CampTemplateDTO;
import org.rotary.exchange.backend.dto.CampTemplateRequest;
import org.rotary.exchange.backend.exception.ErrorResponse;
import org.rotary.exchange.backend.security.service.UserPrinciple;
import org.rotary.exchange.backend.service.CampTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "Camp Templates", description = "Manage reusable camp templates owned by coordinators")
public class CampTemplateController {

    private final CampTemplateService templateService;

    @Operation(
            summary = "Get my templates",
            description = "Retrieves all camp templates owned by the authenticated coordinator.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<List<CampTemplateDTO>> getMyTemplates(
            @Parameter(hidden = true) Authentication authentication) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        
        if (isAdmin(authentication)) {
            // Admin sees all templates
            return ResponseEntity.ok(templateService.getAllTemplates());
        }
        
        // Coordinator sees only their own templates
        return ResponseEntity.ok(templateService.getTemplatesForCoordinator(user.getId()));
    }

    @Operation(
            summary = "Get template by ID",
            description = "Retrieves a single camp template. Coordinators can only access their own templates.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to access this template"),
            @ApiResponse(responseCode = "404", description = "Template not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CampTemplateDTO> getTemplateById(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Integer id) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(templateService.getTemplateById(id, user.getId(), isAdmin(authentication)));
    }

    @Operation(
            summary = "Create new template",
            description = "Creates a new camp template owned by the authenticated coordinator.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<CampTemplateDTO> createTemplate(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody CampTemplateRequest request) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(templateService.createTemplate(request, user.getId()));
    }

    @Operation(
            summary = "Update template",
            description = "Updates an existing camp template. Coordinators can only update their own templates.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this template"),
            @ApiResponse(responseCode = "404", description = "Template not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CampTemplateDTO> updateTemplate(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Integer id,
            @RequestBody CampTemplateRequest request) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(templateService.updateTemplate(id, request, user.getId(), isAdmin(authentication)));
    }

    @Operation(
            summary = "Delete template",
            description = "Deletes a camp template. Coordinators can only delete their own templates.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this template"),
            @ApiResponse(responseCode = "404", description = "Template not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Integer id) {
        UserPrinciple user = (UserPrinciple) authentication.getPrincipal();
        templateService.deleteTemplate(id, user.getId(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
