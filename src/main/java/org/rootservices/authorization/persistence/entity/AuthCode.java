package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/8/15.
 */
public class AuthCode {
    private UUID uuid;
    private byte[] code;
    private UUID resourceOwnerUUID;
    private UUID clientUUID;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public AuthCode() {}

    public AuthCode(UUID uuid, byte[] code, UUID resourceOwnerUUID, UUID clientUUID, OffsetDateTime expiresAt) {
        this.uuid = uuid;
        this.code = code;
        this.resourceOwnerUUID = resourceOwnerUUID;
        this.clientUUID = clientUUID;
        this.expiresAt = expiresAt;
    }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public UUID getResourceOwnerUUID() {
        return resourceOwnerUUID;
    }

    public void setResourceOwnerUUID(UUID resourceOwnerUUID) {
        this.resourceOwnerUUID = resourceOwnerUUID;
    }

    public UUID getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(UUID clientUUID) {
        this.clientUUID = clientUUID;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
