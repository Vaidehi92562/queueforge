package com.queueforge.queue;

import com.queueforge.common.BaseEntity;
import com.queueforge.project.Project;
import com.queueforge.retry.RetryPolicy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "queues")
public class JobQueue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retry_policy_id")
    private RetryPolicy retryPolicy;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false)
    private int priority = 5;

    @Column(nullable = false, name = "max_concurrency")
    private int maxConcurrency = 5;

    @Column(nullable = false, name = "is_paused")
    private boolean paused = false;

    @Column(nullable = false, name = "rate_limit_per_minute")
    private int rateLimitPerMinute = 100;

    public JobQueue() {
    }

    public JobQueue(
            Project project,
            RetryPolicy retryPolicy,
            String name,
            int priority,
            int maxConcurrency,
            boolean paused,
            int rateLimitPerMinute
    ) {
        this.project = project;
        this.retryPolicy = retryPolicy;
        this.name = name;
        this.priority = priority;
        this.maxConcurrency = maxConcurrency;
        this.paused = paused;
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
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

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }
}
