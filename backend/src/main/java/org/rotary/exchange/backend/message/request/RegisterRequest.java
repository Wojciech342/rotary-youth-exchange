package org.rotary.exchange.backend.message.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for coordinator registration")
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Coordinator email address", example = "john.smith@rotary.org", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Coordinator first name", example = "John", minLength = 2, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Coordinator last name", example = "Smith", minLength = 2, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Account password (min 8 characters)", example = "SecurePass123", minLength = 8, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
