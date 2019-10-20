package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/18/15.
 */
public class Scope {
    private UUID id;
    private String name;
    private OffsetDateTime createdAt;

    public Scope(){}

    public Scope(UUID id, String name) {
        this.id = id;
        this.name = name;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
