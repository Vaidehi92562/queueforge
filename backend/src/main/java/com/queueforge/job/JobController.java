package com.queueforge.job;

import com.queueforge.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/immediate")
    public ApiResponse<JobResponse> createImmediateJob(@Valid @RequestBody CreateJobRequest request) {
        JobResponse response = jobService.createImmediateJob(request);
        return ApiResponse.success("Immediate job created successfully", response);
    }

    @PostMapping("/delayed")
    public ApiResponse<JobResponse> createDelayedJob(@Valid @RequestBody CreateJobRequest request) {
        JobResponse response = jobService.createDelayedJob(request);
        return ApiResponse.success("Delayed job created successfully", response);
    }

    @PostMapping("/scheduled")
    public ApiResponse<JobResponse> createScheduledJob(@Valid @RequestBody CreateJobRequest request) {
        JobResponse response = jobService.createScheduledJob(request);
        return ApiResponse.success("Scheduled job created successfully", response);
    }

    @PostMapping("/recurring")
    public ApiResponse<ScheduledJobResponse> createRecurringJob(@Valid @RequestBody CreateRecurringJobRequest request) {
        ScheduledJobResponse response = jobService.createRecurringJob(request);
        return ApiResponse.success("Recurring job created successfully", response);
    }

    @PostMapping("/batch")
    public ApiResponse<List<JobResponse>> createBatchJobs(@Valid @RequestBody BatchJobRequest request) {
        List<JobResponse> response = jobService.createBatchJobs(request);
        return ApiResponse.success("Batch jobs created successfully", response);
    }

    @GetMapping
    public ApiResponse<List<JobResponse>> getJobs(
            @RequestParam UUID projectId,
            @RequestParam(required = false) JobStatus status
    ) {
        List<JobResponse> response = jobService.getJobs(projectId, status);
        return ApiResponse.success("Jobs fetched successfully", response);
    }

    @GetMapping("/{jobId}")
    public ApiResponse<JobResponse> getJobById(@PathVariable UUID jobId) {
        JobResponse response = jobService.getJobById(jobId);
        return ApiResponse.success("Job fetched successfully", response);
    }
}
