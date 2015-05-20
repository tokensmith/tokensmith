package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/18/15.
 */
public class Scope {
    private UUID uuid;
    private String name;
    private OffsetDateTime createdAt;

    public Scope(){}

    public Scope(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
