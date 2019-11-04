package net.tokensmith.repository.entity;

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
    private Long accessTokenCodeSecondsToExpiry;
    private Long accessTokenTokenSecondsToExpiry;
    private Long accessTokenPasswordSecondsToExpiry;
    private Long accessTokenRefreshSecondsToExpiry;
    private Long accessTokenClientSecondsToExpiry;
    private Long authorizationCodeSecondsToExpiry;
    private Long refreshTokenSecondsToExpiry;
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

    public Long getAccessTokenCodeSecondsToExpiry() {
        return accessTokenCodeSecondsToExpiry;
    }

    public void setAccessTokenCodeSecondsToExpiry(Long accessTokenCodeSecondsToExpiry) {
        this.accessTokenCodeSecondsToExpiry = accessTokenCodeSecondsToExpiry;
    }

    public Long getAccessTokenTokenSecondsToExpiry() {
        return accessTokenTokenSecondsToExpiry;
    }

    public void setAccessTokenTokenSecondsToExpiry(Long accessTokenTokenSecondsToExpiry) {
        this.accessTokenTokenSecondsToExpiry = accessTokenTokenSecondsToExpiry;
    }

    public Long getAccessTokenPasswordSecondsToExpiry() {
        return accessTokenPasswordSecondsToExpiry;
    }

    public void setAccessTokenPasswordSecondsToExpiry(Long accessTokenPasswordSecondsToExpiry) {
        this.accessTokenPasswordSecondsToExpiry = accessTokenPasswordSecondsToExpiry;
    }

    public Long getAccessTokenRefreshSecondsToExpiry() {
        return accessTokenRefreshSecondsToExpiry;
    }

    public void setAccessTokenRefreshSecondsToExpiry(Long accessTokenRefreshSecondsToExpiry) {
        this.accessTokenRefreshSecondsToExpiry = accessTokenRefreshSecondsToExpiry;
    }

    public Long getAccessTokenClientSecondsToExpiry() {
        return accessTokenClientSecondsToExpiry;
    }

    public void setAccessTokenClientSecondsToExpiry(Long accessTokenClientSecondsToExpiry) {
        this.accessTokenClientSecondsToExpiry = accessTokenClientSecondsToExpiry;
    }

    public Long getAuthorizationCodeSecondsToExpiry() {
        return authorizationCodeSecondsToExpiry;
    }

    public void setAuthorizationCodeSecondsToExpiry(Long authorizationCodeSecondsToExpiry) {
        this.authorizationCodeSecondsToExpiry = authorizationCodeSecondsToExpiry;
    }

    public Long getRefreshTokenSecondsToExpiry() {
        return refreshTokenSecondsToExpiry;
    }

    public void setRefreshTokenSecondsToExpiry(Long refreshTokenSecondsToExpiry) {
        this.refreshTokenSecondsToExpiry = refreshTokenSecondsToExpiry;
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
