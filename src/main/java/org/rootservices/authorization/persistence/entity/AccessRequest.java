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
    private UUID resourceOwnerUUID;
    private UUID clientUUID;
    private Optional<URI> redirectURI;
    private List<AccessRequestScope> accessRequestScopes;
    private OffsetDateTime createdAt;

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


    public List<AccessRequestScope> getAccessRequestScopes() {
        return accessRequestScopes;
    }

    public void setAccessRequestScopes(List<AccessRequestScope> accessRequestScopes) {
        this.accessRequestScopes = accessRequestScopes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
