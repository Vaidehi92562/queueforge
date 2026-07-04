package com.queueforge.job;

import com.queueforge.organization.OrganizationMemberRepository;
import com.queueforge.project.Project;
import com.queueforge.queue.JobQueue;
import com.queueforge.queue.JobQueueRepository;
import com.queueforge.scheduler.ScheduledJob;
import com.queueforge.scheduler.ScheduledJobRepository;
import com.queueforge.security.CurrentUserService;
import com.queueforge.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobLogRepository jobLogRepository;
    private final JobQueueRepository jobQueueRepository;
    private final ScheduledJobRepository scheduledJobRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public JobService(
            JobRepository jobRepository,
            JobLogRepository jobLogRepository,
            JobQueueRepository jobQueueRepository,
            ScheduledJobRepository scheduledJobRepository,
            OrganizationMemberRepository organizationMemberRepository,
            CurrentUserService currentUserService
    ) {
        this.jobRepository = jobRepository;
        this.jobLogRepository = jobLogRepository;
        this.jobQueueRepository = jobQueueRepository;
        this.scheduledJobRepository = scheduledJobRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public JobResponse createImmediateJob(CreateJobRequest request) {
        request.setType(JobType.IMMEDIATE);
        request.setRunAt(Instant.now());

        return createExecutableJob(request, JobStatus.QUEUED);
    }

    @Transactional
    public JobResponse createDelayedJob(CreateJobRequest request) {
        if (request.getRunAt() == null) {
            throw new IllegalArgumentException("Run time is required for delayed jobs");
        }

        if (request.getRunAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Run time for delayed jobs must be in the future");
        }

        request.setType(JobType.DELAYED);

        return createExecutableJob(request, JobStatus.SCHEDULED);
    }

    @Transactional
    public JobResponse createScheduledJob(CreateJobRequest request) {
        if (request.getRunAt() == null) {
            throw new IllegalArgumentException("Run time is required for scheduled jobs");
        }

        request.setType(JobType.SCHEDULED);

        return createExecutableJob(request, JobStatus.SCHEDULED);
    }

    @Transactional
    public List<JobResponse> createBatchJobs(BatchJobRequest request) {
        return request.getJobs()
                .stream()
                .map(jobRequest -> {
                    jobRequest.setType(JobType.BATCH);

                    if (jobRequest.getRunAt() == null) {
                        jobRequest.setRunAt(Instant.now());
                    }

                    return createExecutableJob(jobRequest, JobStatus.QUEUED);
                })
                .toList();
    }

    @Transactional
    public ScheduledJobResponse createRecurringJob(CreateRecurringJobRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        JobQueue queue = jobQueueRepository.findById(request.getQueueId())
                .orElseThrow(() -> new IllegalArgumentException("Queue not found"));

        validateProjectAccess(queue.getProject(), currentUser.getId());

        ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setQueue(queue);
        scheduledJob.setProject(queue.getProject());
        scheduledJob.setName(request.getName().trim());
        scheduledJob.setCronExpression(request.getCronExpression().trim());
        scheduledJob.setPayloadTemplate(request.getPayloadTemplate());
        scheduledJob.setActive(true);
        scheduledJob.setNextRunAt(request.getNextRunAt() == null ? Instant.now() : request.getNextRunAt());

        ScheduledJob savedScheduledJob = scheduledJobRepository.save(scheduledJob);

        return toScheduledJobResponse(savedScheduledJob);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobs(UUID projectId, JobStatus status) {
        User currentUser = currentUserService.getCurrentUser();

        List<Job> jobs;

        if (status == null) {
            jobs = jobRepository.findByProjectId(projectId);
        } else {
            jobs = jobRepository.findByProjectIdAndStatus(projectId, status);
        }

        if (jobs.size() == 0) {
            return List.of();
        }

        validateProjectAccess(jobs.get(0).getProject(), currentUser.getId());

        return jobs.stream()
                .map(this::toJobResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        User currentUser = currentUserService.getCurrentUser();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        validateProjectAccess(job.getProject(), currentUser.getId());

        return toJobResponse(job);
    }

    private JobResponse createExecutableJob(CreateJobRequest request, JobStatus status) {
        User currentUser = currentUserService.getCurrentUser();

        if (request.getIdempotencyKey() != null && request.getIdempotencyKey().isBlank() == false) {
            Job existingJob = jobRepository.findByIdempotencyKey(request.getIdempotencyKey().trim())
                    .orElse(null);

            if (existingJob != null) {
                validateProjectAccess(existingJob.getProject(), currentUser.getId());
                return toJobResponse(existingJob);
            }
        }

        JobQueue queue = jobQueueRepository.findById(request.getQueueId())
                .orElseThrow(() -> new IllegalArgumentException("Queue not found"));

        validateProjectAccess(queue.getProject(), currentUser.getId());

        Job job = new Job();
        job.setQueue(queue);
        job.setProject(queue.getProject());
        job.setType(request.getType());
        job.setStatus(status);
        job.setPayload(request.getPayload());
        job.setPriority(request.getPriority());
        job.setRunAt(request.getRunAt() == null ? Instant.now() : request.getRunAt());
        job.setCronExpression(request.getCronExpression());
        job.setCurrentAttempt(0);

        if (queue.getRetryPolicy() != null) {
            job.setMaxAttempts(queue.getRetryPolicy().getMaxAttempts());
        } else {
            job.setMaxAttempts(3);
        }

        if (request.getIdempotencyKey() != null && request.getIdempotencyKey().isBlank () == false) {
            job.setIdempotencyKey(request.getIdempotencyKey().trim());
        }

        Job savedJob = jobRepository.save(job);

        createJobLog(savedJob, "Job created with status " + savedJob.getStatus());

        return toJobResponse(savedJob);
    }

    private void createJobLog(Job job, String message) {
        JobLog log = new JobLog();
        log.setJob(job);
        log.setLogLevel(JobLogLevel.INFO);
        log.setMessage(message);

        jobLogRepository.save(log);
    }

    private void validateProjectAccess(Project project, UUID userId) {
        UUID organizationId = project.getOrganization().getId();

        boolean isMember = organizationMemberRepository.existsByOrganizationIdAndUserId(
                organizationId,
                userId
        );

        if (isMember == false) {
            throw new IllegalArgumentException("You do not have access to this project");
        }
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

    private ScheduledJobResponse toScheduledJobResponse(ScheduledJob scheduledJob) {
        return new ScheduledJobResponse(
                scheduledJob.getId(),
                scheduledJob.getQueue().getId(),
                scheduledJob.getProject().getId(),
                scheduledJob.getName(),
                scheduledJob.getCronExpression(),
                scheduledJob.getPayloadTemplate(),
                scheduledJob.isActive(),
                scheduledJob.getNextRunAt(),
                scheduledJob.getLastRunAt(),
                scheduledJob.getCreatedAt(),
                scheduledJob.getUpdatedAt()
        );
    }
}
