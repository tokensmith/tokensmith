package net.tokensmith.authorization.persistence.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/14/15.
 */
public class AccessRequest {
    private UUID id;
    private UUID resourceOwnerId;
    private UUID clientId;
    private Optional<URI> redirectURI;
    private List<AccessRequestScope> accessRequestScopes;
    private OffsetDateTime createdAt;

    public AccessRequest() {}

    public AccessRequest(UUID id, UUID resourceOwnerId, UUID clientId, Optional<URI> redirectURI) {
        this.id = id;
        this.resourceOwnerId = resourceOwnerId;
        this.clientId = clientId;
        this.redirectURI = redirectURI;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(UUID resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
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
