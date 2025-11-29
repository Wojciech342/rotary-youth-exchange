package org.rotary.exchange.backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility for managing secure HttpOnly cookies for refresh tokens.
 * 
 * Security features:
 * - HttpOnly: JavaScript cannot access the cookie (XSS protection)
 * - Secure: Cookie only sent over HTTPS (in production)
 * - SameSite=Strict: CSRF protection
 * - Path=/api/auth: Cookie only sent to auth endpoints
 */
@Component
public class CookieUtil {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${jwt.refresh-expiration:604800}")
    private int refreshTokenExpirationSeconds;

    /**
     * Create a secure HttpOnly cookie containing the refresh token
     */
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);           // Cannot be accessed by JavaScript
        cookie.setSecure(secureCookie);     // Only send over HTTPS in production
        cookie.setPath("/api/auth");        // Only sent to auth endpoints
        cookie.setMaxAge(refreshTokenExpirationSeconds);
        // SameSite is set via response header since Cookie class doesn't support it directly
        return cookie;
    }

    /**
     * Create a cookie that clears the refresh token (for logout)
     */
    public Cookie createRefreshTokenClearCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);  // Immediately expires
        return cookie;
    }

    /**
     * Add cookie to response with SameSite attribute
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = createRefreshTokenCookie(refreshToken);
        response.addCookie(cookie);
        // Add SameSite=Strict via header (not directly supported by Cookie class)
        addSameSiteAttribute(response, cookie);
    }

    /**
     * Clear the refresh token cookie
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = createRefreshTokenClearCookie();
        response.addCookie(cookie);
    }

    /**
     * Extract refresh token from cookies
     */
    public Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isEmpty())
                .findFirst();
    }

    /**
     * Add SameSite attribute to cookie via Set-Cookie header
     */
    private void addSameSiteAttribute(HttpServletResponse response, Cookie cookie) {
        StringBuilder cookieHeader = new StringBuilder();
        cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());
        cookieHeader.append("; Path=").append(cookie.getPath());
        cookieHeader.append("; Max-Age=").append(cookie.getMaxAge());
        cookieHeader.append("; HttpOnly");
        if (cookie.getSecure()) {
            cookieHeader.append("; Secure");
        }
        cookieHeader.append("; SameSite=Strict");
        
        response.addHeader("Set-Cookie", cookieHeader.toString());
    }
}
