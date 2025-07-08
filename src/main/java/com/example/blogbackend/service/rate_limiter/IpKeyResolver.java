package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IpKeyResolver implements RateLimitKeyResolver {
  @Override
  public Optional<String> resolveKey(HttpServletRequest request) {
    return Optional.of("IP_" + request.getRemoteAddr());
  }
}
