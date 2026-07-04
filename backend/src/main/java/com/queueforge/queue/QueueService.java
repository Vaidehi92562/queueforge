package com.queueforge.queue;

import com.queueforge.job.JobRepository;
import com.queueforge.job.JobStatus;
import com.queueforge.organization.OrganizationMemberRepository;
import com.queueforge.project.Project;
import com.queueforge.project.ProjectRepository;
import com.queueforge.retry.RetryPolicy;
import com.queueforge.retry.RetryPolicyRepository;
import com.queueforge.security.CurrentUserService;
import com.queueforge.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QueueService {

    private final JobQueueRepository jobQueueRepository;
    private final JobRepository jobRepository;
    private final ProjectRepository projectRepository;
    private final RetryPolicyRepository retryPolicyRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public QueueService(
            JobQueueRepository jobQueueRepository,
            JobRepository jobRepository,
            ProjectRepository projectRepository,
            RetryPolicyRepository retryPolicyRepository,
            OrganizationMemberRepository organizationMemberRepository,
            CurrentUserService currentUserService
    ) {
        this.jobQueueRepository = jobQueueRepository;
        this.jobRepository = jobRepository;
        this.projectRepository = projectRepository;
        this.retryPolicyRepository = retryPolicyRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public QueueResponse createQueue(CreateQueueRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found")); 

        validateProjectAccess(project, currentUser.getId());

        String normalizedName = request.getName().trim();

        if (jobQueueRepository.existsByNameAndProjectId(normalizedName, project.getId())) {
            throw new IllegalArgumentException("Queue with this name already exists in this project");
        }

        RetryPolicy retryPolicy = null;

        if (request.getRetryPolicyId() != null) {
            retryPolicy = retryPolicyRepository.findById(request.getRetryPolicyId())
                    .orElseThrow(() -> new IllegalArgumentException("Retry policy not found"));
        }

        JobQueue queue = new JobQueue();
        queue.setProject(project);
        queue.setRetryPolicy(retryPolicy);
        queue.setName(normalizedName);
        queue.setPriority(request.getPriority());
        queue.setMaxConcurrency(request.getMaxConcurrency());
        queue.setPaused(false);
        queue.setRateLimitPerMinute(request.getRateLimitPerMinute());

        JobQueue savedQueue = jobQueueRepository.save(queue);

        return toResponse(savedQueue);
    }

    @Transactional(readOnly = true)
    public List<QueueResponse> getQueuesByProject(UUID projectId) {
        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found")); 

        validateProjectAccess(project, currentUser.getId());

        return jobQueueRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public QueueResponse pauseQueue(UUID queueId) {
        User currentUser = currentUserService.getCurrentUser();

        JobQueue queue = jobQueueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue not found")); 

        validateProjectAccess(queue.getProject(), currentUser.getId());

        queue.setPaused(true);

        JobQueue savedQueue = jobQueueRepository.save(queue);

        return toResponse(savedQueue);
    }

    @Transactional
    public QueueResponse resumeQueue(UUID queueId) {
        User currentUser = currentUserService.getCurrentUser();

        JobQueue queue = jobQueueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue not found")); 

        validateProjectAccess(queue.getProject(), currentUser.getId());

        queue.setPaused(false);

        JobQueue savedQueue = jobQueueRepository.save(queue);

        return toResponse(savedQueue);
    }

    @Transactional(readOnly = true)
    public QueueStatsResponse getQueueStats(UUID queueId) {
        User currentUser = currentUserService.getCurrentUser();

        JobQueue queue = jobQueueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue not found")); 

        validateProjectAccess(queue.getProject(), currentUser.getId());

        return new QueueStatsResponse(
                queue.getId(),
                queue.getName(),
                queue.isPaused(),
                queue.getPriority(),
                queue.getMaxConcurrency(),
                queue.getRateLimitPerMinute(),
                jobRepository.countByQueueIdAndStatus(queue.getId(), JobStatus.QUEUED),
                jobRepository.countByQueueIdAndStatus(queue.getId(), JobStatus.RUNNING),
                jobRepository.countByQueueIdAndStatus(queue.getId(), JobStatus.COMPLETED),
                jobRepository.countByQueueIdAndStatus(queue.getId(), JobStatus.FAILED),
                jobRepository.countByQueueIdAndStatus(queue.getId(), JobStatus.DEAD_LETTER)
        );
    }

    private void validateProjectAccess(Project project, UUID userId) {
        UUID organizationId = project.getOrganization().getId();

        boolean isMember = organizationMemberRepository.existsByOrganizationIdAndUserId(
                organizationId,
                userId
        );

        if (!isMember) {
            throw new IllegalArgumentException("You do not have access to this project");
        }
    }

    private QueueResponse toResponse(JobQueue queue) {
        UUID retryPolicyId = queue.getRetryPolicy() == null
                ? null
                : queue.getRetryPolicy().getId();

        return new QueueResponse(
                queue.getId(),
                queue.getProject().getId(),
                retryPolicyId,
                queue.getName(),
                queue.getPriority(),
                queue.getMaxConcurrency(),
                queue.isPaused(),
                queue.getRateLimitPerMinute(),
                queue.getCreatedAt(),
                queue.getUpdatedAt()
        );
    }
}
