package com.example.blogbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.example.blogbackend.repository")
@SpringBootApplication
public class BlogBackendApplication {
  public static final String API_VERSION_1 = "/api/v1";

  public static void main(String[] args) {
    SpringApplication.run(BlogBackendApplication.class, args);
  }
}
