package org.rotary.exchange.backend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.rotary.exchange.backend.security.service.UserDetailsServiceImpl;
import org.rotary.exchange.backend.security.service.UserPrinciple;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter - runs on every request.
 * Extracts JWT from Authorization header, validates it, and sets up SecurityContext.
 * 
 * Optimization: If token contains embedded claims (userId, roles), uses them directly
 * without a database lookup. Falls back to DB lookup for legacy tokens.
 */
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                UsernamePasswordAuthenticationToken authentication;
                
                // Check if token has embedded claims (new tokens)
                if (tokenProvider.hasEmbeddedClaims(jwt)) {
                    // Fast path: use claims from token, no DB lookup
                    authentication = createAuthenticationFromClaims(jwt);
                } else {
                    // Legacy path: load from database
                    authentication = createAuthenticationFromDatabase(jwt);
                }
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Create authentication from JWT claims (no DB lookup - fast)
     */
    private UsernamePasswordAuthenticationToken createAuthenticationFromClaims(String jwt) {
        Integer userId = tokenProvider.getUserIdFromToken(jwt);
        String email = tokenProvider.getUserNameFromJwtToken(jwt);
        List<String> roles = tokenProvider.getRolesFromToken(jwt);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create a lightweight UserPrinciple from claims
        UserPrinciple userPrinciple = new UserPrinciple(
                userId, 
                email, 
                null,  // firstName not needed for auth
                null,  // lastName not needed for auth
                null,  // password not needed for auth
                authorities
        );

        return new UsernamePasswordAuthenticationToken(userPrinciple, null, authorities);
    }

    /**
     * Create authentication from database (fallback for legacy tokens)
     */
    private UsernamePasswordAuthenticationToken createAuthenticationFromDatabase(String jwt) {
        String username = tokenProvider.getUserNameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

