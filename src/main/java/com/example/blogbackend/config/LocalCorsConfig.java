package com.example.blogbackend.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS configuration for local development environment. This allows requests from the local
 * development React app running on localhost:5173 and 127.0.0.1:5173.
 */
@Configuration
@Profile("local")
public class LocalCorsConfig {

  /**
   * CORS configuration for local development. This allows requests from the local development react
   * app running on localhost:5173 and 127.0.0.1:5173.
   *
   * @return CorsConfigurationSource
   * @see <a href="https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html">Spring
   *     CORS Documentation</a>
   */
  @Bean(name = "localCorsConfigurationSource")
  public CorsConfigurationSource localCorsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
