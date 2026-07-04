package com.queueforge.metrics;

public class DashboardMetricsResponse {

    private long totalQueues;
    private long totalJobs;
    private long queuedJobs;
    private long scheduledJobs;
    private long runningJobs;
    private long completedJobs;
    private long failedJobs;
    private long retryingJobs;
    private long deadLetterJobs;
    private long totalWorkers;
    private long onlineWorkers;
    private long drainingWorkers;
    private long offlineWorkers;

    public DashboardMetricsResponse(
            long totalQueues,
            long totalJobs,
            long queuedJobs,
            long scheduledJobs,
            long runningJobs,
            long completedJobs,
            long failedJobs,
            long retryingJobs,
            long deadLetterJobs,
            long totalWorkers,
            long onlineWorkers,
            long drainingWorkers,
            long offlineWorkers
    ) {
        this.totalQueues = totalQueues;
        this.totalJobs = totalJobs;
        this.queuedJobs = queuedJobs;
        this.scheduledJobs = scheduledJobs;
        this.runningJobs = runningJobs;
        this.completedJobs = completedJobs;
        this.failedJobs = failedJobs;
        this.retryingJobs = retryingJobs;
        this.deadLetterJobs = deadLetterJobs;
        this.totalWorkers = totalWorkers;
        this.onlineWorkers = onlineWorkers;
        this.drainingWorkers = drainingWorkers;
        this.offlineWorkers = offlineWorkers;
    }

    public long getTotalQueues() {
        return totalQueues;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public long getQueuedJobs() {
        return queuedJobs;
    }

    public long getScheduledJobs() {
        return scheduledJobs;
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

    public long getRetryingJobs() {
        return retryingJobs;
    }

    public long getDeadLetterJobs() {
        return deadLetterJobs;
    }

    public long getTotalWorkers() {
        return totalWorkers;
    }

    public long getOnlineWorkers() {
        return onlineWorkers;
    }

    public long getDrainingWorkers() {
        return drainingWorkers;
    }

    public long getOfflineWorkers() {
        return offlineWorkers;
    }
}
