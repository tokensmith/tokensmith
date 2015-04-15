package org.rootservices.authorization.persistence.entity;

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
    private Date expiresAt;
    private Date createdAt;

    public AuthCode() {}

    public AuthCode(UUID uuid, byte[] code, UUID resourceOwnerUUID, UUID clientUUID, Date expiresAt) {
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

    public UUID getClientUUDI() {
        return clientUUID;
    }

    public void setClientUUID(UUID clientUUID) {
        this.clientUUID = clientUUID;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
