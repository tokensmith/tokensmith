package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/12/15.
 */
public class ClientScope {
    private UUID uuid;
    private UUID clientUUID;
    private UUID scopeUUID;
    private OffsetDateTime createdAt;

    public ClientScope(UUID uuid, UUID clientUUID, UUID scopeUUID) {
        this.uuid = uuid;
        this.clientUUID = clientUUID;
        this.scopeUUID = scopeUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(UUID clientUUID) {
        this.clientUUID = clientUUID;
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
