package com.queueforge.worker;

import com.queueforge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "worker_nodes")
public class WorkerNode extends BaseEntity {

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private WorkerStatus status = WorkerStatus.ONLINE;

    @Column(nullable = false, name = "max_concurrency")
    private int maxConcurrency = 5;

    @Column(nullable = false, name = "current_load")
    private int currentLoad = 0;

    @Column(nullable = false, name = "started_at")
    private Instant startedAt = Instant.now();

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column(nullable = false, name = "shutdown_requested")
    private boolean shutdownRequested = false;

    public WorkerNode() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(Instant lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }

    public boolean isShutdownRequested() {
        return shutdownRequested;
    }

    public void setShutdownRequested(boolean shutdownRequested) {
        this.shutdownRequested = shutdownRequested;
    }
}
