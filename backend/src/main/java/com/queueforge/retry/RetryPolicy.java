package com.queueforge.retry;

import com.queueforge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "retry_policies")
public class RetryPolicy extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RetryStrategy strategy;

    @Column(nullable = false, name = "max_attempts")
    private int maxAttempts;

    @Column(nullable = false, name = "base_delay_seconds")
    private int baseDelaySeconds;

    @Column(nullable = false, name = "max_delay_seconds")
    private int maxDelaySeconds;

    public RetryPolicy() {
    }

    public RetryPolicy(
            String name,
            RetryStrategy strategy,
            int maxAttempts,
            int baseDelaySeconds,
            int maxDelaySeconds
    ) {
        this.name = name;
        this.strategy = strategy;
        this.maxAttempts = maxAttempts;
        this.baseDelaySeconds = baseDelaySeconds;
        this.maxDelaySeconds = maxDelaySeconds;
    }

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
