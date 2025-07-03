package com.example.blogbackend.provider;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class TimeProvider {
  public Instant getNow() {
    return Instant.now();
  }
}
