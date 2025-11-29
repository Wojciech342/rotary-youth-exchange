package org.rotary.exchange.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.CoordinatorDTO;
import org.rotary.exchange.backend.message.request.LoginRequest;
import org.rotary.exchange.backend.message.request.RegisterRequest;
import org.rotary.exchange.backend.message.request.TokenRefreshRequest;
import org.rotary.exchange.backend.message.response.JwtResponse;
import org.rotary.exchange.backend.message.response.ResponseMessage;
import org.rotary.exchange.backend.message.response.TokenRefreshResponse;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.security.CookieUtil;
import org.rotary.exchange.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and session management")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "Login",
            description = """
                    Authenticates a coordinator and returns JWT tokens.
                    
                    The access token is returned in the response body and should be stored in memory.
                    The refresh token is set as an HttpOnly cookie for security.
                    
                    Access tokens expire in 15 minutes. Use /refresh to get a new one.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            @Parameter(hidden = true) HttpServletResponse response) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        cookieUtil.addRefreshTokenCookie(response, jwtResponse.getRefreshToken());
        jwtResponse.setRefreshToken(null);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "Register new coordinator",
            description = "Creates a new coordinator account. The default role is COORDINATOR."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ResponseMessage("Coordinator registered successfully."));
    }

    @Operation(
            summary = "Refresh access token",
            description = """
                    Gets a new access token using the refresh token.
                    
                    The refresh token is automatically read from the HttpOnly cookie.
                    Alternatively, you can pass it in the request body (for non-browser clients).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "No refresh token provided"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @RequestBody(required = false) TokenRefreshRequest bodyRequest) {
        
        String refreshToken = cookieUtil.getRefreshTokenFromCookies(request)
                .orElseGet(() -> bodyRequest != null ? bodyRequest.getRefreshToken() : null);
        
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        TokenRefreshRequest tokenRequest = new TokenRefreshRequest();
        tokenRequest.setRefreshToken(refreshToken);
        
        TokenRefreshResponse tokenResponse = authService.refreshToken(tokenRequest);
        cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());
        tokenResponse.setRefreshToken(null);
        
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "Logout",
            description = "Revokes the refresh token and clears the cookie. The access token remains valid until expiration."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @RequestBody(required = false) TokenRefreshRequest bodyRequest) {
        
        String refreshToken = cookieUtil.getRefreshTokenFromCookies(request)
                .orElseGet(() -> bodyRequest != null ? bodyRequest.getRefreshToken() : null);
        
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }
        
        cookieUtil.clearRefreshTokenCookie(response);
        return ResponseEntity.ok(new ResponseMessage("Logged out successfully."));
    }

    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all refresh tokens for the current user, logging them out from all devices."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out from all devices"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout-all")
    public ResponseEntity<ResponseMessage> logoutAll(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) HttpServletResponse response) {
        authService.logoutAll(authentication);
        cookieUtil.clearRefreshTokenCookie(response);
        return ResponseEntity.ok(new ResponseMessage("Logged out from all devices successfully."));
    }

    @Operation(
            summary = "Get current user",
            description = "Returns the profile of the currently authenticated coordinator."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user profile",
                    content = @Content(schema = @Schema(implementation = CoordinatorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<CoordinatorDTO> getCurrentCoordinator(
            @Parameter(hidden = true) Authentication authentication) {
        Coordinator coordinator = authService.getCurrentCoordinator(authentication);
        return ResponseEntity.ok(new CoordinatorDTO(coordinator));
    }
}