package org.baseservices.persistence.entity;

import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/22/14.
 */
public class AuthUser {

    private UUID uuid;
    private String email;
    private byte[] password;
    private Date createdAt;

    public AuthUser() {}

    public AuthUser(UUID uuid, String email, byte[] password) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
