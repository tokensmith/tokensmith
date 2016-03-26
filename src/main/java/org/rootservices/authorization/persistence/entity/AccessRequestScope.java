package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/19/15.
 */
public class AccessRequestScope {
    private UUID uuid;
    private UUID accessRequestUUID;
    private Scope scope;
    private OffsetDateTime createdAt;

    public AccessRequestScope() {}

    public AccessRequestScope(UUID uuid, UUID accessRequestUUID, Scope scope) {
        this.uuid = uuid;
        this.accessRequestUUID = accessRequestUUID;
        this.scope = scope;
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

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
