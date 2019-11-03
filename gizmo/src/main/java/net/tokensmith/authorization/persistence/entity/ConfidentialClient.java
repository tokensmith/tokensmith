package net.tokensmith.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public class ConfidentialClient {
    private UUID id;
    private Client client;
    private String password;
    private OffsetDateTime createdAt;

    public ConfidentialClient() {}

    public ConfidentialClient(UUID id, Client client, String password) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
