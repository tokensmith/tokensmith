package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/19/16.
 */
public class ResourceOwnerToken {
    private UUID id;
    private ResourceOwner resourceOwner;
    private Token token;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ResourceOwner getResourceOwner() {
        return resourceOwner;
    }

    public void setResourceOwner(ResourceOwner resourceOwner) {
        this.resourceOwner = resourceOwner;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
