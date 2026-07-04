package com.queueforge.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    List<Organization> findByOwnerId(UUID ownerId);

    boolean existsByNameAndOwnerId(String name, UUID ownerId);
}
