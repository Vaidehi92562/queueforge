package com.queueforge.organization;

import java.time.Instant;
import java.util.UUID;

public class OrganizationResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    public OrganizationResponse(
            UUID id,
            String name,
            String description,
            UUID ownerId,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
