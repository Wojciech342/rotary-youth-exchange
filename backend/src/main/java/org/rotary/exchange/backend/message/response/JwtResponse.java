package org.rotary.exchange.backend.message.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;


@Getter
@Setter
@Schema(description = "Response containing JWT tokens after successful authentication")
public class JwtResponse {

    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Refresh token for obtaining new access tokens", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
    
    @Schema(description = "Token type, always 'Bearer'", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "Authenticated user's email/username", example = "john.smith@rotary.org")
    private String username;
    
    @Schema(description = "User's granted authorities/roles", example = "[{\"authority\": \"ROLE_COORDINATOR\"}]")
    private Collection<? extends GrantedAuthority> authorities;

    public JwtResponse(String accessToken, String refreshToken, String username, Collection<? extends GrantedAuthority> authorities) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.authorities = authorities;
    }
}
