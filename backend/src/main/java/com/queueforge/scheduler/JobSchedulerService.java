package com.queueforge.scheduler;

import com.queueforge.job.Job;
import com.queueforge.job.JobLog;
import com.queueforge.job.JobLogLevel;
import com.queueforge.job.JobLogRepository;
import com.queueforge.job.JobRepository;
import com.queueforge.job.JobStatus;
import com.queueforge.job.JobType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class JobSchedulerService {

    private final JobRepository jobRepository;
    private final JobLogRepository jobLogRepository;
    private final ScheduledJobRepository scheduledJobRepository;

    public JobSchedulerService(
            JobRepository jobRepository,
            JobLogRepository jobLogRepository,
            ScheduledJobRepository scheduledJobRepository
    ) {
        this.jobRepository = jobRepository;
        this.jobLogRepository = jobLogRepository;
        this.scheduledJobRepository = scheduledJobRepository;
    }

    @Scheduled(fixedDelayString = "${queueforge.scheduler.scan-interval-ms:5000}")
    @Transactional
    public void promoteDueJobsToQueue() {
        Instant now = Instant.now();

        List<Job> dueScheduledJobs = jobRepository
                .findByStatusAndRunAtLessThanEqualOrderByRunAtAsc(JobStatus.SCHEDULED, now);

        List<Job> dueRetryingJobs = jobRepository
                .findByStatusAndRunAtLessThanEqualOrderByRunAtAsc(JobStatus.RETRYING, now);

        promoteJobs(dueScheduledJobs, "Scheduled job is now ready and moved to queue");
        promoteJobs(dueRetryingJobs, "Retry delay completed; job moved back to queue");
    }

    @Scheduled(fixedDelayString = "${queueforge.scheduler.scan-interval-ms:5000}")
    @Transactional
    public void generateRecurringJobs() {
        Instant now = Instant.now();

        List<ScheduledJob> dueRecurringJobs = scheduledJobRepository
                .findByActiveTrueAndNextRunAtLessThanEqual(now);

        for (ScheduledJob scheduledJob : dueRecurringJobs) {
            Job generatedJob = new Job();
            generatedJob.setQueue(scheduledJob.getQueue());
            generatedJob.setProject(scheduledJob.getProject());
            generatedJob.setType(JobType.RECURRING);
            generatedJob.setStatus(JobStatus.QUEUED);
            generatedJob.setPayload(scheduledJob.getPayloadTemplate());
            generatedJob.setPriority(scheduledJob.getQueue().getPriority());
            generatedJob.setRunAt(now);
            generatedJob.setCronExpression(scheduledJob.getCronExpression());
            generatedJob.setCurrentAttempt(0);

            if (scheduledJob.getQueue().getRetryPolicy() == null) {
                generatedJob.setMaxAttempts(3);
            } else {
                generatedJob.setMaxAttempts(scheduledJob.getQueue().getRetryPolicy().getMaxAttempts());
            }

            Job savedGeneratedJob = jobRepository.save(generatedJob);

            JobLog log = new JobLog();
            log.setJob(savedGeneratedJob);
            log.setExecution(null);
            log.setLogLevel(JobLogLevel.INFO);
            log.setMessage("Recurring job generated from schedule " + scheduledJob.getName());
            jobLogRepository.save(log);

            scheduledJob.setLastRunAt(now);
            scheduledJob.setNextRunAt(calculateNextRunAt(scheduledJob.getCronExpression(), now));
            scheduledJobRepository.save(scheduledJob);
        }
    }

    private void promoteJobs(List<Job> jobs, String logMessage) {
        for (Job job : jobs) {
            job.setStatus(JobStatus.QUEUED);
            job.setLockedByWorkerId(null);
            job.setLockedAt(null);

            Job savedJob = jobRepository.save(job);

            JobLog log = new JobLog();
            log.setJob(savedJob);
            log.setExecution(null);
            log.setLogLevel(JobLogLevel.INFO);
            log.setMessage(logMessage);

            jobLogRepository.save(log);
        }
    }

    private Instant calculateNextRunAt(String cronExpression, Instant fromTime) {
        CronExpression parsedCron = CronExpression.parse(cronExpression);
        ZonedDateTime fromDateTime = ZonedDateTime.ofInstant(fromTime, ZoneOffset.UTC);
        ZonedDateTime nextDateTime = parsedCron.next(fromDateTime);

        if (nextDateTime == null) {
            return fromTime.plusSeconds(3600);
        }

        return nextDateTime.toInstant();
    }
}
