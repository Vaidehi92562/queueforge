package com.queueforge.worker;

import com.queueforge.job.Job;
import com.queueforge.job.JobExecution;
import com.queueforge.job.JobExecutionRepository;
import com.queueforge.job.JobLog;
import com.queueforge.job.JobLogLevel;
import com.queueforge.job.JobLogRepository;
import com.queueforge.job.JobRepository;
import com.queueforge.job.JobResponse;
import com.queueforge.job.JobStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkerService {

    private final WorkerNodeRepository workerNodeRepository;
    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobLogRepository jobLogRepository;

    public WorkerService(
            WorkerNodeRepository workerNodeRepository,
            JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository,
            JobLogRepository jobLogRepository
    ) {
        this.workerNodeRepository = workerNodeRepository;
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobLogRepository = jobLogRepository;
    }

    @Transactional
    public WorkerResponse registerWorker(RegisterWorkerRequest request) {
        String normalizedName = request.getName().trim();

        WorkerNode worker = workerNodeRepository.findByName(normalizedName)
                .orElse(null);

        if (worker == null) {
            worker = new WorkerNode();
            worker.setName(normalizedName);
            worker.setStartedAt(Instant.now());
        }

        worker.setStatus(WorkerStatus.ONLINE);
        worker.setMaxConcurrency(request.getMaxConcurrency());
        worker.setCurrentLoad(0);
        worker.setLastHeartbeatAt(Instant.now());
        worker.setShutdownRequested(false);

        WorkerNode savedWorker = workerNodeRepository.save(worker);

        return toResponse(savedWorker);
    }

    @Transactional
    public WorkerResponse heartbeat(UUID workerId, WorkerHeartbeatRequest request) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        if (worker.isShutdownRequested()) {
            worker.setStatus(WorkerStatus.DRAINING);
        } else {
            worker.setStatus(WorkerStatus.ONLINE);
        }

        worker.setCurrentLoad(request.getCurrentLoad());
        worker.setLastHeartbeatAt(Instant.now());

        WorkerNode savedWorker = workerNodeRepository.save(worker);

        return toResponse(savedWorker);
    }

    @Transactional
    public WorkerResponse requestShutdown(UUID workerId) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        worker.setShutdownRequested(true);
        worker.setStatus(WorkerStatus.DRAINING);

        WorkerNode savedWorker = workerNodeRepository.save(worker);

        return toResponse(savedWorker);
    }

    @Transactional
    public ClaimJobsResponse claimJobs(UUID workerId, ClaimJobsRequest request) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        if (worker.isShutdownRequested() || worker.getStatus() != WorkerStatus.ONLINE) {
            throw new IllegalArgumentException("Worker is not available for claiming jobs");
        }

        int availableCapacity = worker.getMaxConcurrency() - worker.getCurrentLoad();

        if (availableCapacity <= 0) {
            return new ClaimJobsResponse(worker.getId(), request.getMaxJobs(), 0, List.of());
        }

        int claimLimit = Math.min(request.getMaxJobs(), availableCapacity);

        List<Job> claimableJobs = jobRepository.findClaimableJobsForUpdate(claimLimit);
        Instant now = Instant.now();

        for (Job job : claimableJobs) {
            int nextAttempt = job.getCurrentAttempt() + 1;

            job.setStatus(JobStatus.CLAIMED);
            job.setLockedByWorkerId(worker.getId());
            job.setLockedAt(now);
            job.setClaimedAt(now);
            job.setCurrentAttempt(nextAttempt);

            JobExecution execution = new JobExecution();
            execution.setJob(job);
            execution.setWorkerId(worker.getId());
            execution.setAttemptNumber(nextAttempt);
            execution.setStatus(JobStatus.CLAIMED);
            execution.setStartedAt(now);

            jobExecutionRepository.save(execution);

            createJobLog(job, execution, JobLogLevel.INFO, "Job claimed by worker " + worker.getName());
        }

        jobRepository.saveAll(claimableJobs);

        worker.setCurrentLoad(worker.getCurrentLoad() + claimableJobs.size());
        worker.setLastHeartbeatAt(now);
        workerNodeRepository.save(worker);

        List<JobResponse> claimedJobResponses = claimableJobs.stream()
                .map(this::toJobResponse)
                .toList();

        return new ClaimJobsResponse(
                worker.getId(),
                request.getMaxJobs(),
                claimedJobResponses.size(),
                claimedJobResponses
        );
    }

    @Transactional
    public JobResponse startJob(UUID workerId, UUID jobId) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.CLAIMED) {
            throw new IllegalArgumentException("Only CLAIMED jobs can be started");
        }

        if (workerId.equals(job.getLockedByWorkerId()) == false) {
            throw new IllegalArgumentException("This job is not claimed by this worker");
        }

        Instant now = Instant.now();

        job.setStatus(JobStatus.RUNNING);
        job.setStartedAt(now);

        JobExecution execution = jobExecutionRepository.findFirstByJobIdOrderByAttemptNumberDesc(job.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job execution record not found"));

        execution.setStatus(JobStatus.RUNNING);
        execution.setStartedAt(now);

        createJobLog(job, execution, JobLogLevel.INFO, "Job started by worker " + worker.getName());

        jobExecutionRepository.save(execution);
        Job savedJob = jobRepository.save(job);

        worker.setLastHeartbeatAt(now);
        workerNodeRepository.save(worker);

        return toJobResponse(savedJob);
    }

    @Transactional
    public JobResponse completeJob(UUID workerId, UUID jobId, CompleteJobRequest request) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.RUNNING) {
            throw new IllegalArgumentException("Only RUNNING jobs can be completed");
        }

        if (workerId.equals(job.getLockedByWorkerId()) == false) {
            throw new IllegalArgumentException("This job is not running on this worker");
        }

        Instant now = Instant.now();

        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(now);
        job.setLockedByWorkerId(null);
        job.setLockedAt(null);

        JobExecution execution = jobExecutionRepository.findFirstByJobIdOrderByAttemptNumberDesc(job.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job execution record not found"));

        execution.setStatus(JobStatus.COMPLETED);
        execution.setCompletedAt(now);

        if (execution.getStartedAt() != null) {
            execution.setDurationMs(Duration.between(execution.getStartedAt(), now).toMillis());
        }

        String message = request.getResultMessage() == null || request.getResultMessage().isBlank()
                ? "Job completed successfully"
                : request.getResultMessage();

        createJobLog(job, execution, JobLogLevel.INFO, message);

        jobExecutionRepository.save(execution);
        Job savedJob = jobRepository.save(job);

        worker.setCurrentLoad(Math.max(0, worker.getCurrentLoad() - 1));
        worker.setLastHeartbeatAt(now);
        workerNodeRepository.save(worker);

        return toJobResponse(savedJob);
    }

    @Transactional
    public JobResponse failJob(UUID workerId, UUID jobId, FailJobRequest request) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.RUNNING) {
            throw new IllegalArgumentException("Only RUNNING jobs can be failed");
        }

        if (workerId.equals(job.getLockedByWorkerId()) == false) {
            throw new IllegalArgumentException("This job is not running on this worker");
        }

        Instant now = Instant.now();

        JobExecution execution = jobExecutionRepository.findFirstByJobIdOrderByAttemptNumberDesc(job.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job execution record not found"));

        execution.setStatus(JobStatus.FAILED);
        execution.setCompletedAt(now);
        execution.setErrorMessage(request.getErrorMessage());

        if (execution.getStartedAt() != null) {
            execution.setDurationMs(Duration.between(execution.getStartedAt(), now).toMillis());
        }

        job.setLastErrorMessage(request.getErrorMessage());
        job.setLockedByWorkerId(null);
        job.setLockedAt(null);

        boolean attemptsLeft = job.getCurrentAttempt() < job.getMaxAttempts();

        if (attemptsLeft) {
            long retryDelaySeconds = calculateRetryDelaySeconds(job);
            job.setStatus(JobStatus.RETRYING);
            job.setRunAt(now.plusSeconds(retryDelaySeconds));

            createJobLog(
                    job,
                    execution,
                    JobLogLevel.WARN,
                    "Job failed and scheduled for retry after " + retryDelaySeconds + " seconds. Error: " + request.getErrorMessage()
            );
        } else {
            job.setStatus(JobStatus.DEAD_LETTER);
            job.setCompletedAt(now);

            createJobLog(
                    job,
                    execution,
                    JobLogLevel.ERROR,
                    "Job moved to dead letter queue after max attempts. Error: " + request.getErrorMessage()
            );
        }

        jobExecutionRepository.save(execution);
        Job savedJob = jobRepository.save(job);

        worker.setCurrentLoad(Math.max(0, worker.getCurrentLoad() - 1));
        worker.setLastHeartbeatAt(now);
        workerNodeRepository.save(worker);

        return toJobResponse(savedJob);
    }

    private long calculateRetryDelaySeconds(Job job) {
        if (job.getQueue().getRetryPolicy() == null) {
            return 30L;
        }

        int attempt = Math.max(1, job.getCurrentAttempt());
        long baseDelay = job.getQueue().getRetryPolicy().getBaseDelaySeconds();
        long maxDelay = job.getQueue().getRetryPolicy().getMaxDelaySeconds();

        long calculatedDelay;

        switch (job.getQueue().getRetryPolicy().getStrategy()) {
            case FIXED_DELAY:
                calculatedDelay = baseDelay;
                break;
            case LINEAR_BACKOFF:
                calculatedDelay = baseDelay * attempt;
                break;
            case EXPONENTIAL_BACKOFF:
                calculatedDelay = baseDelay * (long) Math.pow(2, attempt - 1);
                break;
            default:
                calculatedDelay = baseDelay;
        }

        return Math.min(calculatedDelay, maxDelay);
    }

    @Transactional(readOnly = true)
    public List<WorkerResponse> getWorkers() {
        return workerNodeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkerResponse getWorkerById(UUID workerId) {
        WorkerNode worker = workerNodeRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        return toResponse(worker);
    }

    private void createJobLog(Job job, JobExecution execution, JobLogLevel level, String message) {
        JobLog log = new JobLog();
        log.setJob(job);
        log.setExecution(execution);
        log.setLogLevel(level);
        log.setMessage(message);

        jobLogRepository.save(log);
    }

    private WorkerResponse toResponse(WorkerNode worker) {
        return new WorkerResponse(
                worker.getId(),
                worker.getName(),
                worker.getStatus(),
                worker.getMaxConcurrency(),
                worker.getCurrentLoad(),
                worker.getStartedAt(),
                worker.getLastHeartbeatAt(),
                worker.isShutdownRequested(),
                worker.getCreatedAt(),
                worker.getUpdatedAt()
        );
    }

    private JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getQueue().getId(),
                job.getProject().getId(),
                job.getType(),
                job.getStatus(),
                job.getPayload(),
                job.getPriority(),
                job.getRunAt(),
                job.getCronExpression(),
                job.getMaxAttempts(),
                job.getCurrentAttempt(),
                job.getIdempotencyKey(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
