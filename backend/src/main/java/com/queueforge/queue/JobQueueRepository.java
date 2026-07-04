package com.queueforge.queue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobQueueRepository extends JpaRepository<JobQueue, UUID> {

    List<JobQueue> findByProjectId(UUID projectId);

    boolean existsByNameAndProjectId(String name, UUID projectId);
}
