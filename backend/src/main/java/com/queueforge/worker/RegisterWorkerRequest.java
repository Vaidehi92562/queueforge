package com.queueforge.worker;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RegisterWorkerRequest {

    @NotBlank(message = "Worker name is required")
    private String name;

    @Min(value = 1, message = "Max concurrency must be at least 1")
    @Max(value = 100, message = "Max concurrency cannot exceed 100")
    private int maxConcurrency = 5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }
}
