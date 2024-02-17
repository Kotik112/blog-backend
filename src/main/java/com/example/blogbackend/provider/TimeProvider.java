package com.example.blogbackend.provider;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimeProvider {
    public Instant getNow() {
        return Instant.now();
    }
}
