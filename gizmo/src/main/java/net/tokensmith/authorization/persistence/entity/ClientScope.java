package net.tokensmith.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/12/15.
 */
public class ClientScope {
    private UUID id;
    private UUID clientId;
    private UUID scopeId;
    private OffsetDateTime createdAt;

    public ClientScope(UUID id, UUID clientId, UUID scopeId) {
        this.id = id;
        this.clientId = clientId;
        this.scopeId = scopeId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getScopeId() {
        return scopeId;
    }

    public void setScopeId(UUID scopeId) {
        this.scopeId = scopeId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
