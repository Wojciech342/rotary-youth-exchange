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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.exception.ErrorResponse;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.Country;
import org.rotary.exchange.backend.model.District;
import org.rotary.exchange.backend.repository.CountryRepository;
import org.rotary.exchange.backend.repository.DistrictRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Country and nested district operations")
public class CountryController {

    private final CountryRepository countryRepo;
    private final DistrictRepository districtRepo;

    @Operation(
            summary = "Get all countries",
            description = "Retrieves all countries that participate in the Rotary Youth Exchange program"
    )
    @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    @GetMapping
    public List<Country> getAllCountries() {
        return countryRepo.findAll();
    }

    @Operation(
            summary = "Get country by ID",
            description = "Retrieves a specific country by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country found"),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Country getCountryById(
            @Parameter(description = "Country ID", required = true, example = "1")
            @PathVariable Integer id) {
        return countryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
    }

    @Operation(
            summary = "Get districts by country",
            description = "Retrieves all Rotary districts within a specific country"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Districts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/districts")
    public List<District> getDistrictsByCountry(
            @Parameter(description = "Country ID", required = true, example = "1")
            @PathVariable Integer id) {
        // Verify country exists
        if (!countryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Country", "id", id);
        }
        return districtRepo.findByCountryId(id);
    }

    @Operation(
            summary = "Create country",
            description = "Creates a new country. Admin only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Country> createCountry(@Valid @RequestBody CountryRequest request) {
        Country country = new Country();
        country.setName(request.getName());
        country.setPdfVisible(request.getPdfVisible() != null ? request.getPdfVisible() : true);
        return ResponseEntity.ok(countryRepo.save(country));
    }

    @Operation(
            summary = "Update country",
            description = "Updates an existing country. Admin only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Country> updateCountry(
            @Parameter(description = "Country ID", required = true, example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody CountryRequest request) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        
        country.setName(request.getName());
        if (request.getPdfVisible() != null) {
            country.setPdfVisible(request.getPdfVisible());
        }
        return ResponseEntity.ok(countryRepo.save(country));
    }

    @Operation(
            summary = "Delete country",
            description = "Deletes a country. Will fail if there are districts associated with this country. Admin only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Country deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Country not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete - country has associated districts")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCountry(
            @Parameter(description = "Country ID", required = true, example = "1")
            @PathVariable Integer id) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        
        // Check if there are districts associated with this country
        if (!districtRepo.findByCountryId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete country with associated districts. Delete districts first.");
        }
        
        countryRepo.delete(country);
        return ResponseEntity.noContent().build();
    }

    @Data
    @Schema(description = "Country creation/update request")
    static class CountryRequest {
        @NotBlank(message = "Country name is required")
        @Schema(description = "Name of the country", example = "Germany", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "Whether PDF flyers are visible for this country", example = "true")
        private Boolean pdfVisible;
    }
}
