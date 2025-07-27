package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class IpKeyResolver implements RateLimitKeyResolver {
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(IpKeyResolver.class);

  @Override
  public Optional<String> resolveKey(HttpServletRequest request) {
    if (request == null || request.getRemoteAddr() == null) {
      logger.warn("Request or remote address is null, cannot resolve IP key.");
      return Optional.empty();
    }
    logger.debug("Resolving key for IP: {}", request.getRemoteAddr());
    return Optional.of("IP_" + request.getRemoteAddr());
  }
}
