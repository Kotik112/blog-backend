package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionKeyResolver implements RateLimitKeyResolver {
  private final Logger logger = LoggerFactory.getLogger(SessionKeyResolver.class);

  @Override
  public Optional<String> resolveKey(HttpServletRequest request) {
    logger.debug("Resolving key for session: {}", request.getRequestURI());
    HttpSession session = request.getSession(false);
    return session != null ? Optional.of("SESSION_" + session.getId()) : Optional.empty();
  }
}
