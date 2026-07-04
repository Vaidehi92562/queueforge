package com.queueforge.worker;

import com.queueforge.job.JobResponse;

import java.util.List;
import java.util.UUID;

public class ClaimJobsResponse {

    private UUID workerId;
    private int requestedJobs;
    private int claimedJobs;
    private List<JobResponse> jobs;

    public ClaimJobsResponse(
            UUID workerId,
            int requestedJobs,
            int claimedJobs,
            List<JobResponse> jobs
    ) {
        this.workerId = workerId;
        this.requestedJobs = requestedJobs;
        this.claimedJobs = claimedJobs;
        this.jobs = jobs;
    }

    public UUID getWorkerId() {
        return workerId;
    }

    public int getRequestedJobs() {
        return requestedJobs;
    }

    public int getClaimedJobs() {
        return claimedJobs;
    }

    public List<JobResponse> getJobs() {
        return jobs;
    }
}
