package com.example.blogbackend.service.rate_limiter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
  private final RateLimiterService rateLimiterService;

  public RateLimitFilter(RateLimiterService rateLimiterService) {
    this.rateLimiterService = rateLimiterService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Bucket bucket = rateLimiterService.resolveBucket(request);
    if (bucket.tryConsume(1)) {
      // Proceed with the request if the token is consumed successfully
      filterChain.doFilter(request, response);
    } else {
      // If the rate limit is exceeded, send a 429 Too Many Requests response
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.TEXT_PLAIN_VALUE);
      response.getWriter().write("Rate limit exceeded. Try again later.");
    }
  }
}
