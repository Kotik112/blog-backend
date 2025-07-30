package com.example.blogbackend.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * This is the default CORS configuration for the blog backend application. This configuration
 * allows cross-origin requests from specific frontend applications and is intended for use in the
 * development environment. It is not suitable for production use as it allows all headers and
 * methods.
 */
@SuppressWarnings("unused")
@Configuration
public class DefaultCorsConfig {

  /**
   * CORS configuration for development environment. This allows requests from the development
   * frontend applications. <br>
   * The allowed origins are: <br>
   * - https://blog-frontend-dev1.s3-website-us-east-1.amazonaws.com <br>
   * - https://blogify.kitok.click <br>
   * - https://blogify-dev.kitok.click <br>
   *
   * @return CorsConfigurationSource
   * @see <a href="https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html">Spring
   *     CORS Documentation</a>
   */
  @Bean
  @Primary
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(
        List.of(
            "http://blog-frontend-dev1.s3-website-us-east-1.amazonaws.com",
            "https://blogify.kitok.click",
            "https://blogify-dev.kitok.click"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
