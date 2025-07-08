package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface RateLimitKeyResolver {
  Optional<String> resolveKey(HttpServletRequest request);
}
