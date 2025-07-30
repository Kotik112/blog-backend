package com.example.blogbackend.config;

import static org.springframework.http.HttpMethod.POST;

import com.example.blogbackend.enums.Role;
import com.example.blogbackend.service.DatabaseUserService;
import jakarta.servlet.MultipartConfigElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfigurationSource;

@SuppressWarnings("unused")
@Configuration
public class SecurityConfig {
  private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
    this.corsConfigurationSource = corsConfigurationSource;
  }

    /**
   * Authentication manager bean that uses the in-memory user details service. This can be replaced
   * with a custom UserDetailsService for database-backed users.
   */
  @Bean
  public AuthenticationManager authenticationManager(
      PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authProvider);
  }

  /**
   * Main security filter chain config. - CORS enabled for allowed origins - CSRF disabled for
   * stateless API use - Basic Auth and form login enabled - Endpoint-based authorization
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(POST, "/api/v1/blog/**")
                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/api/v1/blog/logged-in-user")
                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/api/v1/admin/**")
                    .hasRole(Role.ADMIN.name())
                    .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers("/api/v1/contact/**")
                    .permitAll()
                    .requestMatchers("actuator/health", "actuator/info")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        // .httpBasic(Customizer.withDefaults())  // Basic Auth for Postman or frontend fetch
        .formLogin(AbstractHttpConfigurer::disable);

    return http.build();
  }

  /**
   * Multipart configuration for file uploads. Sets maximum file size and request size limits.
   *
   * @see <a
   *     href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-config-multipart">Spring
   *     Multipart Configuration</a>
   * @return MultipartConfigElement
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofMegabytes(10));
    factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    return factory.createMultipartConfig();
  }

  /**
   * Password encoder bean for hashing passwords. BCrypt is a strong hashing algorithm suitable for
   * password storage.
   *
   * @see <a
   *     href="https://docs.spring.io/spring-security/reference/servlet/password-storage.html">Spring
   *     Security Password Storage</a>
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
