package com.sukoon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "idem:";

    // Returns true if key was set successfully (not a duplicate)
    // Returns false if key already exists (duplicate request)
    public boolean acquireLock(String key) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(PREFIX + key, "PROCESSING", 24, TimeUnit.HOURS);
        return Boolean.TRUE.equals(success);
    }

    // Saves the response against the key so next time we can return it
    public void saveResponse(String key, String responseJson) {
        redisTemplate.opsForValue().set(PREFIX + key + ":response", responseJson, 24, TimeUnit.HOURS);
    }
}