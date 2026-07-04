package com.queueforge.worker;

import jakarta.validation.constraints.Min;

public class WorkerHeartbeatRequest {

    @Min(value = 0, message = "Current load cannot be negative")
    private int currentLoad = 0;

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }
}
