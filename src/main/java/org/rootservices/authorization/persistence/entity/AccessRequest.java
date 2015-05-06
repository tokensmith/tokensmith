package org.rootservices.authorization.persistence.entity;

import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/14/15.
 */
public class AccessRequest {
    private UUID uuid;
    private Optional<URI> redirectURI;
    private UUID authCodeUUID;
    private Date createdAt;

    public AccessRequest() {}

    public AccessRequest(UUID uuid, Optional<URI> redirectURI, UUID authCodeUUID) {
        this.uuid = uuid;
        this.redirectURI = redirectURI;
        this.authCodeUUID = authCodeUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Optional<URI> getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(Optional<URI> redirectURI) {
        this.redirectURI = redirectURI;
    }

    public UUID getAuthCodeUUID() {
        return authCodeUUID;
    }

    public void setAuthCodeUUID(UUID authCodeUUID) {
        this.authCodeUUID = authCodeUUID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
