package com.queueforge.metrics;

import com.queueforge.job.JobRepository;
import com.queueforge.job.JobStatus;
import com.queueforge.queue.JobQueueRepository;
import com.queueforge.worker.WorkerNodeRepository;
import com.queueforge.worker.WorkerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MetricsService {

    private final JobQueueRepository jobQueueRepository;
    private final JobRepository jobRepository;
    private final WorkerNodeRepository workerNodeRepository;

    public MetricsService(
            JobQueueRepository jobQueueRepository,
            JobRepository jobRepository,
            WorkerNodeRepository workerNodeRepository
    ) {
        this.jobQueueRepository = jobQueueRepository;
        this.jobRepository = jobRepository;
        this.workerNodeRepository = workerNodeRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics() {
        long totalQueues = jobQueueRepository.count();

        long totalJobs = jobRepository.count();
        long queuedJobs = jobRepository.countByStatus(JobStatus.QUEUED);
        long scheduledJobs = jobRepository.countByStatus(JobStatus.SCHEDULED);
        long runningJobs = jobRepository.countByStatus(JobStatus.RUNNING);
        long completedJobs = jobRepository.countByStatus(JobStatus.COMPLETED);
        long failedJobs = jobRepository.countByStatus(JobStatus.FAILED);
        long retryingJobs = jobRepository.countByStatus(JobStatus.RETRYING);
        long deadLetterJobs = jobRepository.countByStatus(JobStatus.DEAD_LETTER);

        long totalWorkers = workerNodeRepository.count();
        long onlineWorkers = workerNodeRepository.countByStatus(WorkerStatus.ONLINE);
        long drainingWorkers = workerNodeRepository.countByStatus(WorkerStatus.DRAINING);
        long offlineWorkers = workerNodeRepository.countByStatus(WorkerStatus.OFFLINE);

        return new DashboardMetricsResponse(
                totalQueues,
                totalJobs,
                queuedJobs,
                scheduledJobs,
                runningJobs,
                completedJobs,
                failedJobs,
                retryingJobs,
                deadLetterJobs,
                totalWorkers,
                onlineWorkers,
                drainingWorkers,
                offlineWorkers
        );
    }
}
