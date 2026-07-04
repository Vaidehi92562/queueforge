package com.queueforge.job;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class JobResponse {

    private UUID id;
    private UUID queueId;
    private UUID projectId;
    private JobType type;
    private JobStatus status;
    private Map<String, Object> payload;
    private int priority;
    private Instant runAt;
    private String cronExpression;
    private int maxAttempts;
    private int currentAttempt;
    private String idempotencyKey;
    private Instant createdAt;
    private Instant updatedAt;

    public JobResponse(
            UUID id,
            UUID queueId,
            UUID projectId,
            JobType type,
            JobStatus status,
            Map<String, Object> payload,
            int priority,
            Instant runAt,
            String cronExpression,
            int maxAttempts,
            int currentAttempt,
            String idempotencyKey,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.queueId = queueId;
        this.projectId = projectId;
        this.type = type;
        this.status = status;
        this.payload = payload;
        this.priority = priority;
        this.runAt = runAt;
        this.cronExpression = cronExpression;
        this.maxAttempts = maxAttempts;
        this.currentAttempt = currentAttempt;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getQueueId() {
        return queueId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public JobType getType() {
        return type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public int getPriority() {
        return priority;
    }

    public Instant getRunAt() {
        return runAt;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
