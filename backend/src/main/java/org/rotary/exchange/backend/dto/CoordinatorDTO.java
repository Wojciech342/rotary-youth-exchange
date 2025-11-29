package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.rotary.exchange.backend.model.Coordinator;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Schema(description = "Coordinator profile data transfer object")
public class CoordinatorDTO {
    @Schema(description = "Coordinator ID", example = "1")
    private Integer id;
    
    @Schema(description = "Email address", example = "john.smith@rotary.org")
    private String email;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Smith")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1-555-123-4567")
    private String phone;
    
    @Schema(description = "URL to profile picture", example = "/api/files/images/profile123.jpg")
    private String profilePictureUrl;
    
    @Schema(description = "Coordinator bio/description", example = "Youth exchange coordinator since 2015...")
    private String description;
    
    @Schema(description = "Assigned district ID", example = "1")
    private Integer districtId;
    
    @Schema(description = "Assigned district code", example = "1820")
    private String districtCode;
    
    @Schema(description = "Set of role names", example = "[\"COORDINATOR\", \"ADMIN\"]")
    private Set<String> roles;

    public CoordinatorDTO(Coordinator coordinator) {
        this.id = coordinator.getId();
        this.email = coordinator.getEmail();
        this.firstName = coordinator.getFirstName();
        this.lastName = coordinator.getLastName();
        this.phone = coordinator.getPhone();
        this.profilePictureUrl = coordinator.getProfilePictureUrl();
        this.description = coordinator.getDescription();
        
        if (coordinator.getDistrict() != null) {
            this.districtId = coordinator.getDistrict().getId();
            this.districtCode = coordinator.getDistrict().getCode();
        }
        
        this.roles = coordinator.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
