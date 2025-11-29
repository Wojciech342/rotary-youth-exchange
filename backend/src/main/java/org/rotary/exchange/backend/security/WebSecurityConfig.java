package org.rotary.exchange.backend.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.rotary.exchange.backend.security.jwt.JwtAuthEntryPoint;
import org.rotary.exchange.backend.security.jwt.JwtAuthTokenFilter;
import org.rotary.exchange.backend.security.service.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(unauthorized -> unauthorized
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers(
                                "/",
                                "/error",
                                "/api/auth/**",
                                "/uploads/**"
                        ).permitAll()
                        // Swagger/OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Public endpoints - students can view camps without authentication
                        .requestMatchers(HttpMethod.GET, "/api/camps").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/camps/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/camps/statuses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/countries/**").permitAll()
                        // Districts: public GET for list and single, but access-code endpoints are admin-only
                        .requestMatchers(HttpMethod.GET, "/api/districts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/districts/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/districts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/districts/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/districts/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/districts/{id}/access-code").hasRole("ADMIN")
                        .requestMatchers("/api/districts/{id}/regenerate-access-code").hasRole("ADMIN")
                        .requestMatchers("/api/districts/my-access-code").authenticated()
                        // Countries: public GET, admin for CUD
                        .requestMatchers(HttpMethod.POST, "/api/countries").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/countries/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/countries/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/status/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/coordinators/{id}").permitAll()
                        // Protected template endpoints - all require authentication now
                        .requestMatchers("/api/templates/**").authenticated()
                        // Protected camp endpoints
                        .requestMatchers("/api/camps/my-camps").authenticated()
                        .requestMatchers("/api/camps/district").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/camps/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/camps/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/camps/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/camps/**").authenticated()
                        // Protected status endpoints
                        .requestMatchers(HttpMethod.PATCH, "/api/status/**").authenticated()
                        // Protected coordinator endpoints
                        .requestMatchers("/api/coordinators/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/coordinators").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/coordinators/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/coordinators/{id}/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/coordinators/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}





