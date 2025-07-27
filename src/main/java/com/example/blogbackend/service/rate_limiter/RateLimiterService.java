package com.example.blogbackend.service.rate_limiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
  private final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
  private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
  private final CompositeKeyResolver keyResolver;

  public RateLimiterService(CompositeKeyResolver keyResolver) {
    this.keyResolver = keyResolver;
  }

  public Bucket resolveBucket(HttpServletRequest request) {
    String key = keyResolver.resolveKey(request);
    return bucketCache.computeIfAbsent(key, this::createNewBucket);
  }

  private Bucket createNewBucket(String key) {
    Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
  }
}
