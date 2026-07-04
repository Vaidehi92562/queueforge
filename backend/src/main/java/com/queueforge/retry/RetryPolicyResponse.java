package com.queueforge.retry;

import java.time.Instant;
import java.util.UUID;

public class RetryPolicyResponse {

    private UUID id;
    private String name;
    private RetryStrategy strategy;
    private int maxAttempts;
    private int baseDelaySeconds;
    private int maxDelaySeconds;
    private Instant createdAt;
    private Instant updatedAt;

    public RetryPolicyResponse(
            UUID id,
            String name,
            RetryStrategy strategy,
            int maxAttempts,
            int baseDelaySeconds,
            int maxDelaySeconds,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        this.maxAttempts = maxAttempts;
        this.baseDelaySeconds = baseDelaySeconds;
        this.maxDelaySeconds = maxDelaySeconds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RetryStrategy getStrategy() {
        return strategy;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getBaseDelaySeconds() {
        return baseDelaySeconds;
    }

    public int getMaxDelaySeconds() {
        return maxDelaySeconds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
