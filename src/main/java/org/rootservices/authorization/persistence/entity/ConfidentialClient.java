package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public class ConfidentialClient {
    private UUID id;
    private Client client;
    private byte[] password;
    private OffsetDateTime createdAt;

    public ConfidentialClient() {}

    public ConfidentialClient(UUID id, Client client, byte[] password) {
        this.id = id;
        this.client = client;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
