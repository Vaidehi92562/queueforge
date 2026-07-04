package com.queueforge.worker;

import java.time.Instant;
import java.util.UUID;

public class WorkerResponse {

    private UUID id;
    private String name;
    private WorkerStatus status;
    private int maxConcurrency;
    private int currentLoad;
    private Instant startedAt;
    private Instant lastHeartbeatAt;
    private boolean shutdownRequested;
    private Instant createdAt;
    private Instant updatedAt;

    public WorkerResponse(
            UUID id,
            String name,
            WorkerStatus status,
            int maxConcurrency,
            int currentLoad,
            Instant startedAt,
            Instant lastHeartbeatAt,
            boolean shutdownRequested,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.maxConcurrency = maxConcurrency;
        this.currentLoad = currentLoad;
        this.startedAt = startedAt;
        this.lastHeartbeatAt = lastHeartbeatAt;
        this.shutdownRequested = shutdownRequested;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public boolean isShutdownRequested() {
        return shutdownRequested;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
