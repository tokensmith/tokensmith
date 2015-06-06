package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public class ConfidentialClient {
    private UUID uuid;
    private Client client;
    private byte[] password;
    private OffsetDateTime createdAt;

    public ConfidentialClient() {}

    public ConfidentialClient(UUID uuid, Client client, byte[] password) {
        this.uuid = uuid;
        this.client = client;
        this.password = password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
