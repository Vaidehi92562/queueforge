package com.queueforge.job;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BatchJobRequest {

    @NotEmpty(message = "At least one job is required in batch")
    @Valid
    private List<CreateJobRequest> jobs;

    public List<CreateJobRequest> getJobs() {
        return jobs;
    }

    public void setJobs(List<CreateJobRequest> jobs) {
        this.jobs = jobs;
    }
}
