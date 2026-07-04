package com.queueforge.worker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerNodeRepository extends JpaRepository<WorkerNode, UUID> {

    Optional<WorkerNode> findByName(String name);

    List<WorkerNode> findByStatus(WorkerStatus status);

    long countByStatus(WorkerStatus status);
}
