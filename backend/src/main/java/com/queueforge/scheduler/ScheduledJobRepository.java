package com.queueforge.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, UUID> {

    List<ScheduledJob> findByProjectId(UUID projectId);

    List<ScheduledJob> findByActiveTrueAndNextRunAtLessThanEqual(Instant now);
}
