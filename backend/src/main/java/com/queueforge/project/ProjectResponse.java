package com.queueforge.project;

import java.time.Instant;
import java.util.UUID;

public class ProjectResponse {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public ProjectResponse(
            UUID id,
            UUID organizationId,
            String name,
            String description,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
