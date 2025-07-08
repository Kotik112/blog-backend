package com.example.blogbackend.service.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SessionKeyResolver implements RateLimitKeyResolver {
  @Override
  public Optional<String> resolveKey(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null ? Optional.of("SESSION_" + session.getId()) : Optional.empty();
  }
}
