package com.queueforge.retry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRetryPolicyRequest {

    @NotBlank(message = "Retry policy name is required")
    @Size(min = 2, max = 120, message = "Retry policy name must be between 2 and 120 characters")
    private String name;

    @NotNull(message = "Retry strategy is required")
    private RetryStrategy strategy;

    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 20, message = "Max attempts cannot exceed 20")
    private int maxAttempts;

    @Min(value = 0, message = "Base delay seconds cannot be negative")
    @Max(value = 86400, message = "Base delay seconds cannot exceed 86400")
    private int baseDelaySeconds;

    @Min(value = 0, message = "Max delay seconds cannot be negative")
    @Max(value = 86400, message = "Max delay seconds cannot exceed 86400")
    private int maxDelaySeconds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RetryStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RetryStrategy strategy) {
        this.strategy = strategy;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getBaseDelaySeconds() {
        return baseDelaySeconds;
    }

    public void setBaseDelaySeconds(int baseDelaySeconds) {
        this.baseDelaySeconds = baseDelaySeconds;
    }

    public int getMaxDelaySeconds() {
        return maxDelaySeconds;
    }

    public void setMaxDelaySeconds(int maxDelaySeconds) {
        this.maxDelaySeconds = maxDelaySeconds;
    }
}
