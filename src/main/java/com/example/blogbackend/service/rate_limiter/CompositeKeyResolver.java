package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CompositeKeyResolver {

  private final List<RateLimitKeyResolver> resolvers;

  public CompositeKeyResolver(List<RateLimitKeyResolver> resolvers) {
    this.resolvers = resolvers;
  }

  public String resolveKey(HttpServletRequest request) {
    return resolvers.stream()
        .map(resolver -> resolver.resolveKey(request))
        .filter(java.util.Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No key could be resolved"));
  }
}
