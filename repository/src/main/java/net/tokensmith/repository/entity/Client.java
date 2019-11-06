package net.tokensmith.repository.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/15/14.
 */
public class Client {
    private UUID id;
    private List<ResponseType> responseTypes;
    private URI redirectURI;
    private List<Scope> scopes;
    private OffsetDateTime createdAt;

    public Client() {};

    public Client(UUID id, URI redirectURI) {
        this.id = id;
        this.redirectURI = redirectURI;
    }

    public Client(UUID id, List<ResponseType> responseTypes, URI redirectURI) {
        this.id = id;
        this.responseTypes = responseTypes;
        this.redirectURI = redirectURI;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public List<ResponseType> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<ResponseType> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public URI getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(URI redirectURI) {
        this.redirectURI = redirectURI;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
