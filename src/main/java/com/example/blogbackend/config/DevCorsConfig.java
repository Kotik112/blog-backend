package com.example.blogbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@SuppressWarnings("unused")
@Configuration
@Profile("dev")
public class DevCorsConfig {

    @Bean
    public CorsConfigurationSource devCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(
                List.of(
                        "http://blog-frontend-dev1.s3-website-us-east-1.amazonaws.com",
                        "https://blogify.kitok.click",
                        "https://blogify-dev.kitok.click"
                )
        );
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
