package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NonceType {
    private UUID id;
    private String name;
    private Integer secondsToExpiry;
    private OffsetDateTime createdAt;

    public NonceType() {}

    public NonceType(UUID id, String name, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSecondsToExpiry() {
        return secondsToExpiry;
    }

    public void setSecondsToExpiry(Integer secondsToExpiry) {
        this.secondsToExpiry = secondsToExpiry;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
