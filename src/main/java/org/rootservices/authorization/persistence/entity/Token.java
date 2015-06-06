package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
public class Token {
    private UUID uuid;
    private UUID authCodeUUID;
    private byte[] token;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public Token() {}

    public Token(UUID uuid, UUID authCodeUUID, byte[] token, OffsetDateTime expiresAt) {
        this.uuid = uuid;
        this.authCodeUUID = authCodeUUID;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getAuthCodeUUID() {
        return authCodeUUID;
    }

    public void setAuthCodeUUID(UUID authCodeUUID) {
        this.authCodeUUID = authCodeUUID;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
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
}
