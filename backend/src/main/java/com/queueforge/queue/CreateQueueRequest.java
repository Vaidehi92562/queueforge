package com.queueforge.queue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateQueueRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private UUID retryPolicyId;

    @NotBlank(message = "Queue name is required")
    @Size(min = 2, max = 140, message = "Queue name must be between 2 and 140 characters")
    private String name;

    @Min(value = 0, message = "Priority cannot be negative")
    @Max(value = 100, message = "Priority cannot exceed 100")
    private int priority = 5;

    @Min(value = 1, message = "Max concurrency must be at least 1")
    @Max(value = 100, message = "Max concurrency cannot exceed 100")
    private int maxConcurrency = 5;

    @Min(value = 1, message = "Rate limit must be at least 1 per minute")
    private int rateLimitPerMinute = 100;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getRetryPolicyId() {
        return retryPolicyId;
    }

    public void setRetryPolicyId(UUID retryPolicyId) {
        this.retryPolicyId = retryPolicyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }
}
