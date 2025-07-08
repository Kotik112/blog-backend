package com.example.blogbackend.service.rate_limiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionRateLimiter {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  public Bucket resolveBucket(String sessionId) {
    return buckets.computeIfAbsent(sessionId, this::createNewBucket);
  }

  private Bucket createNewBucket(String sessionId) {
    Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));

    // Build the bucket
    return Bucket.builder().addLimit(limit).build();
  }
}
