package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.rotary.exchange.backend.message.request.LoginRequest;
import org.rotary.exchange.backend.message.request.RegisterRequest;
import org.rotary.exchange.backend.message.request.TokenRefreshRequest;
import org.rotary.exchange.backend.message.response.JwtResponse;
import org.rotary.exchange.backend.message.response.TokenRefreshResponse;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.model.RefreshToken;
import org.rotary.exchange.backend.model.Role;
import org.rotary.exchange.backend.model.RoleName;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.rotary.exchange.backend.repository.RoleRepository;
import org.rotary.exchange.backend.security.jwt.JwtProvider;
import org.rotary.exchange.backend.security.service.UserPrinciple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CoordinatorRepository coordinatorRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String accessToken = jwtProvider.generateJwtToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId());

        return new JwtResponse(accessToken, refreshToken.getToken(), 
                userPrinciple.getUsername(), userPrinciple.getAuthorities());
    }

    /**
     * Refresh the access token using a valid refresh token.
     * Generates a new token with embedded claims (userId, roles) for performance.
     */
    @Transactional(readOnly = true)
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        Coordinator coordinator = refreshToken.getCoordinator();
        
        // Get roles for embedded claims
        List<String> roles = coordinator.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        
        String newAccessToken = jwtProvider.generateTokenWithClaims(
                coordinator.getId(),
                coordinator.getEmail(),
                roles
        );
        
        return new TokenRefreshResponse(newAccessToken, refreshToken.getToken());
    }

    /**
     * Logout - revoke the refresh token
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    /**
     * Logout from all devices - revoke all refresh tokens for this coordinator
     */
    @Transactional
    public void logoutAll(Authentication authentication) {
        Coordinator coordinator = getCurrentCoordinator(authentication);
        refreshTokenService.revokeAllTokensForCoordinator(coordinator);
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (coordinatorRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        Coordinator coordinator = new Coordinator(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        // Set additional fields
        coordinator.setFirstName(registerRequest.getFirstName());
        coordinator.setLastName(registerRequest.getLastName());

        // All new registrations get COORDINATOR role only
        // Admin roles must be assigned manually via database or by existing admins
        Set<Role> roles = new HashSet<>();
        Role coordinatorRole = roleRepository.findByName(RoleName.ROLE_COORDINATOR)
                .orElseThrow(() -> new RuntimeException("Role not found: ROLE_COORDINATOR"));
        roles.add(coordinatorRole);

        coordinator.setRoles(roles);
        coordinatorRepository.save(coordinator);
    }

    public Coordinator getCurrentCoordinator(Authentication authentication) {
        String email = authentication.getName();
        return coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coordinator not found"));
    }

    public Coordinator getCoordinatorByEmail(String email) {
        return coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coordinator not found with email: " + email));
    }
}