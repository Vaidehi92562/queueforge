package com.queueforge.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class CreateRecurringJobRequest {

    @NotNull(message = "Queue ID is required")
    private UUID queueId;

    @NotBlank(message = "Recurring job name is required")
    @Size(min = 2, max = 140, message = "Name must be between 2 and 140 characters")
    private String name;

    @NotBlank(message = "Cron expression is required")
    private String cronExpression;

    @NotNull(message = "Payload template is required")
    private Map<String, Object> payloadTemplate;

    private Instant nextRunAt;

    public UUID getQueueId() {
        return queueId;
    }

    public void setQueueId(UUID queueId) {
        this.queueId = queueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<String, Object> getPayloadTemplate() {
        return payloadTemplate;
    }

    public void setPayloadTemplate(Map<String, Object> payloadTemplate) {
        this.payloadTemplate = payloadTemplate;
    }

    public Instant getNextRunAt() {
        return nextRunAt;
    }

    public void setNextRunAt(Instant nextRunAt) {
        this.nextRunAt = nextRunAt;
    }
}
