package com.queueforge.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByProjectId(UUID projectId);

    List<Job> findByQueueId(UUID queueId);

    List<Job> findByProjectIdAndStatus(UUID projectId, JobStatus status);

    Optional<Job> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    long countByQueueIdAndStatus(UUID queueId, JobStatus status);

    long countByStatus(JobStatus status);

    List<Job> findByStatusAndRunAtLessThanEqualOrderByRunAtAsc(JobStatus status, Instant now);

    @Query(
            value = """
                    SELECT j.*
                    FROM jobs j
                    JOIN queues q ON q.id = j.queue_id
                    WHERE j.status = 'QUEUED'
                      AND q.is_paused = false
                      AND (j.run_at IS NULL OR j.run_at <= NOW())
                    ORDER BY j.priority DESC, j.created_at ASC
                    FOR UPDATE SKIP LOCKED
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<Job> findClaimableJobsForUpdate(@Param("limit") int limit);
}
