package com.queueforge.worker;

import com.queueforge.common.ApiResponse;
import com.queueforge.job.JobResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping("/register")
    public ApiResponse<WorkerResponse> registerWorker(@Valid @RequestBody RegisterWorkerRequest request) {
        WorkerResponse response = workerService.registerWorker(request);
        return ApiResponse.success("Worker registered successfully", response);
    }

    @PostMapping("/{workerId}/heartbeat")
    public ApiResponse<WorkerResponse> heartbeat(
            @PathVariable UUID workerId,
            @Valid @RequestBody WorkerHeartbeatRequest request
    ) {
        WorkerResponse response = workerService.heartbeat(workerId, request);
        return ApiResponse.success("Worker heartbeat updated successfully", response);
    }

    @PostMapping("/{workerId}/claim")
    public ApiResponse<ClaimJobsResponse> claimJobs(
            @PathVariable UUID workerId,
            @Valid @RequestBody ClaimJobsRequest request
    ) {
        ClaimJobsResponse response = workerService.claimJobs(workerId, request);
        return ApiResponse.success("Jobs claimed successfully", response);
    }

    @PostMapping("/{workerId}/jobs/{jobId}/start")
    public ApiResponse<JobResponse> startJob(
            @PathVariable UUID workerId,
            @PathVariable UUID jobId
    ) {
        JobResponse response = workerService.startJob(workerId, jobId);
        return ApiResponse.success("Job started successfully", response);
    }

    @PostMapping("/{workerId}/jobs/{jobId}/complete")
    public ApiResponse<JobResponse> completeJob(
            @PathVariable UUID workerId,
            @PathVariable UUID jobId,
            @Valid @RequestBody CompleteJobRequest request
    ) {
        JobResponse response = workerService.completeJob(workerId, jobId, request);
        return ApiResponse.success("Job completed successfully", response);
    }

    @PostMapping("/{workerId}/jobs/{jobId}/fail")
    public ApiResponse<JobResponse> failJob(
            @PathVariable UUID workerId,
            @PathVariable UUID jobId,
            @Valid @RequestBody FailJobRequest request
    ) {
        JobResponse response = workerService.failJob(workerId, jobId, request);
        return ApiResponse.success("Job failure handled successfully", response);
    }

    @PatchMapping("/{workerId}/shutdown")
    public ApiResponse<WorkerResponse> requestShutdown(@PathVariable UUID workerId) {
        WorkerResponse response = workerService.requestShutdown(workerId);
        return ApiResponse.success("Worker shutdown requested successfully", response);
    }

    @GetMapping
    public ApiResponse<List<WorkerResponse>> getWorkers() {
        List<WorkerResponse> response = workerService.getWorkers();
        return ApiResponse.success("Workers fetched successfully", response);
    }

    @GetMapping("/{workerId}")
    public ApiResponse<WorkerResponse> getWorkerById(@PathVariable UUID workerId) {
        WorkerResponse response = workerService.getWorkerById(workerId);
        return ApiResponse.success("Worker fetched successfully", response);
    }
}
