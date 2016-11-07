package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
public class Configuration {
    private UUID id;
    private Integer accessTokenSize;
    private Integer authorizationCodeSize;
    private Integer refreshTokenSize;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAccessTokenSize() {
        return accessTokenSize;
    }

    public void setAccessTokenSize(Integer accessTokenSize) {
        this.accessTokenSize = accessTokenSize;
    }

    public Integer getAuthorizationCodeSize() {
        return authorizationCodeSize;
    }

    public void setAuthorizationCodeSize(Integer authorizationCodeSize) {
        this.authorizationCodeSize = authorizationCodeSize;
    }

    public Integer getRefreshTokenSize() {
        return refreshTokenSize;
    }

    public void setRefreshTokenSize(Integer refreshTokenSize) {
        this.refreshTokenSize = refreshTokenSize;
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