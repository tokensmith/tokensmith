package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/22/14.
 */
public class ResourceOwner {
    private UUID uuid;
    private String email;
    private byte[] password;
    private Boolean emailVerified;
    private OffsetDateTime createdAt;

    public ResourceOwner() {}

    public ResourceOwner(UUID uuid, String email, byte[] password) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
