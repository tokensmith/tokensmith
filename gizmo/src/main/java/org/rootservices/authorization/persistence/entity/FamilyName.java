package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/19/16.
 */
public class FamilyName {
    private UUID id;
    private UUID resourceOwnerProfileId;
    private String name;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResourceOwnerProfileId() {
        return resourceOwnerProfileId;
    }

    public void setResourceOwnerProfileId(UUID resourceOwnerProfileId) {
        this.resourceOwnerProfileId = resourceOwnerProfileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
