package com.queueforge.retry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RetryPolicyRepository extends JpaRepository<RetryPolicy, UUID> {

    boolean existsByName(String name);
}
