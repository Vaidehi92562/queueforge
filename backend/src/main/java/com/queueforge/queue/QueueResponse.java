package com.queueforge.queue;

import java.time.Instant;
import java.util.UUID;

public class QueueResponse {

    private UUID id;
    private UUID projectId;
    private UUID retryPolicyId;
    private String name;
    private int priority;
    private int maxConcurrency;
    private boolean paused;
    private int rateLimitPerMinute;
    private Instant createdAt;
    private Instant updatedAt;

    public QueueResponse(
            UUID id,
            UUID projectId,
            UUID retryPolicyId,
            String name,
            int priority,
            int maxConcurrency,
            boolean paused,
            int rateLimitPerMinute,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.retryPolicyId = retryPolicyId;
        this.name = name;
        this.priority = priority;
        this.maxConcurrency = maxConcurrency;
        this.paused = paused;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getRetryPolicyId() {
        return retryPolicyId;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
