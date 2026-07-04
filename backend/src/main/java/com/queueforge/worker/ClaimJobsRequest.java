package com.queueforge.worker;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ClaimJobsRequest {

    @Min(value = 1, message = "At least one job must be requested")
    @Max(value = 50, message = "Cannot claim more than 50 jobs at once")
    private int maxJobs = 1;

    public int getMaxJobs() {
        return maxJobs;
    }

    public void setMaxJobs(int maxJobs) {
        this.maxJobs = maxJobs;
    }
}
