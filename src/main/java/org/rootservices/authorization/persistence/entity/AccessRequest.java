package org.rootservices.authorization.persistence.entity;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/14/15.
 */
public class AccessRequest {
    private UUID uuid;
    private UUID resourceOwnerUUID;
    private UUID clientUUID;
    private Optional<URI> redirectURI;
    private Date createdAt;
    private List<Scope> scopes;

    public AccessRequest() {}

    public AccessRequest(UUID uuid, UUID resourceOwnerUUID, UUID clientUUID, Optional<URI> redirectURI) {
        this.uuid = uuid;
        this.resourceOwnerUUID = resourceOwnerUUID;
        this.clientUUID = clientUUID;
        this.redirectURI = redirectURI;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getResourceOwnerUUID() {
        return resourceOwnerUUID;
    }

    public void setResourceOwnerUUID(UUID resourceOwnerUUID) {
        this.resourceOwnerUUID = resourceOwnerUUID;
    }

    public UUID getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(UUID clientUUID) {
        this.clientUUID = clientUUID;
    }

    public Optional<URI> getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(Optional<URI> redirectURI) {
        this.redirectURI = redirectURI;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }
}
