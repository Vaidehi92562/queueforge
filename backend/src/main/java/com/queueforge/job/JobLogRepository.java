package com.queueforge.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobLogRepository extends JpaRepository<JobLog, UUID> {

    List<JobLog> findByJobIdOrderByCreatedAtAsc(UUID jobId);

    List<JobLog> findByExecutionIdOrderByCreatedAtAsc(UUID executionId);
}
