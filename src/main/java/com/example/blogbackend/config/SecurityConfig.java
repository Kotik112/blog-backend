package com.example.blogbackend.config;

import com.example.blogbackend.service.DatabaseUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {

//    /**
//     * In-memory user details service for demo purposes.
//     * In production, use a database or external user management system.
//     */
//    @Bean
//    public UserDetailsService userDetailsServiceInMemory() {
//        UserDetails user = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("password123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    /**
     * Authentication manager bean that uses the in-memory user details service.
     * This can be replaced with a custom UserDetailsService for database-backed users.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    /**
     * Main security filter chain config.
     * - CORS enabled for allowed origins
     * - CSRF disabled for stateless API use
     * - Basic Auth and form login enabled
     * - Endpoint-based authorization
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/api/v1/blog/**").hasRole("USER")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())  // Basic Auth for Postman or frontend fetch
                .formLogin(Customizer.withDefaults()); // Optional: form login for browser

        return http.build();
    }

    /**
     * CORS configuration to allow requests from specific origins.
     * This is necessary for frontend applications to interact with the backend API.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors">Spring CORS Documentation</a>
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Password encoder bean for hashing passwords.
     * BCrypt is a strong hashing algorithm suitable for password storage.
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/password-storage.html">Spring Security Password Storage</a>
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DatabaseUserService databaseUserService) {
        return databaseUserService;
    }
}
