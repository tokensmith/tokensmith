package net.tokensmith.repository.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class LocalToken {
    private UUID id;
    private String token;
    private boolean revoked;
    private UUID resourceOwnerId;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public LocalToken() {}

    public LocalToken(UUID id, String token, UUID resourceOwnerId, OffsetDateTime expiresAt) {
        this.id = id;
        this.token = token;
        this.resourceOwnerId = resourceOwnerId;
        this.expiresAt = expiresAt;
    }

    public LocalToken(UUID id, String token, boolean revoked, UUID resourceOwnerId, OffsetDateTime expiresAt, OffsetDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.revoked = revoked;
        this.resourceOwnerId = resourceOwnerId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public UUID getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(UUID resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
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

    public static class Builder {
        private UUID id;
        private String token;
        private boolean revoked;
        private UUID resourceOwnerId;
        private OffsetDateTime expiresAt;
        private OffsetDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder resourceOwnerId(UUID resourceOwnerId) {
            this.resourceOwnerId = resourceOwnerId;
            return this;
        }

        public Builder expiresAt(OffsetDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LocalToken build() {
            return new LocalToken(id, token, revoked, resourceOwnerId, expiresAt, createdAt);
        }
    }
}
