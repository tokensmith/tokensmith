package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/19/15.
 */
public class AccessRequestScope {
    private UUID uuid;
    private UUID accessRequestUUID;
    private UUID scopeUUID;
    private OffsetDateTime createdAt;

    public AccessRequestScope(UUID uuid, UUID accessRequestUUID, UUID scopeUUID) {
        this.uuid = uuid;
        this.accessRequestUUID = accessRequestUUID;
        this.scopeUUID = scopeUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getAccessRequestUUID() {
        return accessRequestUUID;
    }

    public void setAccessRequestUUID(UUID accessRequestUUID) {
        this.accessRequestUUID = accessRequestUUID;
    }

    public UUID getScopeUUID() {
        return scopeUUID;
    }

    public void setScopeUUID(UUID scopeUUID) {
        this.scopeUUID = scopeUUID;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
