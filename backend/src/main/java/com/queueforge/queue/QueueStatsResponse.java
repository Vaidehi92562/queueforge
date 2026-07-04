package com.queueforge.queue;

import java.util.UUID;

public class QueueStatsResponse {

    private UUID queueId;
    private String queueName;
    private boolean paused;
    private int priority;
    private int maxConcurrency;
    private int rateLimitPerMinute;

    private long queuedJobs;
    private long runningJobs;
    private long completedJobs;
    private long failedJobs;
    private long deadLetterJobs;

    public QueueStatsResponse(
            UUID queueId,
            String queueName,
            boolean paused,
            int priority,
            int maxConcurrency,
            int rateLimitPerMinute,
            long queuedJobs,
            long runningJobs,
            long completedJobs,
            long failedJobs,
            long deadLetterJobs
    ) {
        this.queueId = queueId;
        this.queueName = queueName;
        this.paused = paused;
        this.priority = priority;
        this.maxConcurrency = maxConcurrency;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.queuedJobs = queuedJobs;
        this.runningJobs = runningJobs;
        this.completedJobs = completedJobs;
        this.failedJobs = failedJobs;
        this.deadLetterJobs = deadLetterJobs;
    }

    public UUID getQueueId() {
        return queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getPriority() {
        return priority;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public long getQueuedJobs() {
        return queuedJobs;
    }

    public long getRunningJobs() {
        return runningJobs;
    }

    public long getCompletedJobs() {
        return completedJobs;
    }

    public long getFailedJobs() {
        return failedJobs;
    }

    public long getDeadLetterJobs() {
        return deadLetterJobs;
    }
}
