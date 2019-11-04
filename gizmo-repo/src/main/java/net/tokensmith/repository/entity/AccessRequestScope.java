package net.tokensmith.repository.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/19/15.
 */
public class AccessRequestScope {
    private UUID id;
    private UUID accessRequestId;
    private Scope scope;
    private OffsetDateTime createdAt;

    public AccessRequestScope() {}

    public AccessRequestScope(UUID id, UUID accessRequestId, Scope scope) {
        this.id = id;
        this.accessRequestId = accessRequestId;
        this.scope = scope;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccessRequestId() {
        return accessRequestId;
    }

    public void setAccessRequestId(UUID accessRequestId) {
        this.accessRequestId = accessRequestId;
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
