package com.queueforge.job;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ScheduledJobResponse {

    private UUID id;
    private UUID queueId;
    private UUID projectId;
    private String name;
    private String cronExpression;
    private Map<String, Object> payloadTemplate;
    private boolean active;
    private Instant nextRunAt;
    private Instant lastRunAt;
    private Instant createdAt;
    private Instant updatedAt;

    public ScheduledJobResponse(
            UUID id,
            UUID queueId,
            UUID projectId,
            String name,
            String cronExpression,
            Map<String, Object> payloadTemplate,
            boolean active,
            Instant nextRunAt,
            Instant lastRunAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.queueId = queueId;
        this.projectId = projectId;
        this.name = name;
        this.cronExpression = cronExpression;
        this.payloadTemplate = payloadTemplate;
        this.active = active;
        this.nextRunAt = nextRunAt;
        this.lastRunAt = lastRunAt;
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

    public String getName() {
        return name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public Map<String, Object> getPayloadTemplate() {
        return payloadTemplate;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getNextRunAt() {
        return nextRunAt;
    }

    public Instant getLastRunAt() {
        return lastRunAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
