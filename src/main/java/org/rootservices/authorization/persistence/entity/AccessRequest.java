package org.rootservices.authorization.persistence.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/14/15.
 */
public class AccessRequest {
    private UUID uuid;
    private ResourceOwner resourceOwner;
    private UUID clientUUID;
    private Optional<URI> redirectURI;
    private OffsetDateTime createdAt;
    private List<Scope> scopes;

    public AccessRequest() {}

    public AccessRequest(UUID uuid, ResourceOwner resourceOwner, UUID clientUUID, Optional<URI> redirectURI) {
        this.uuid = uuid;
        this.resourceOwner = resourceOwner;
        this.clientUUID = clientUUID;
        this.redirectURI = redirectURI;
    }

    public AccessRequest(UUID uuid, ResourceOwner resourceOwner, UUID clientUUID, Optional<URI> redirectURI, OffsetDateTime createdAt, List<Scope> scopes) {
        this.uuid = uuid;
        this.resourceOwner = resourceOwner;
        this.clientUUID = clientUUID;
        this.redirectURI = redirectURI;
        this.createdAt = createdAt;
        this.scopes = scopes;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ResourceOwner getResourceOwner() {
        return resourceOwner;
    }

    public void setResourceOwner(ResourceOwner resourceOwner) {
        this.resourceOwner = resourceOwner;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }
}
