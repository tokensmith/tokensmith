package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
public class Token {
    private UUID id;
    private byte[] token;
    private boolean revoked;
    private GrantType grantType;
    private UUID clientId;
    private List<TokenScope> tokenScopes;
    private Long secondsToExpiration;
    private Token leadToken; // optional
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public Token() {}

    public Token(UUID id, byte[] token, OffsetDateTime expiresAt) {
        this.id = id;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public GrantType getGrantType() {
        return grantType;
    }

    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public List<TokenScope> getTokenScopes() {
        return tokenScopes;
    }

    public void setTokenScopes(List<TokenScope> tokenScopes) {
        this.tokenScopes = tokenScopes;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getSecondsToExpiration() {
        return secondsToExpiration;
    }

    public void setSecondsToExpiration(Long secondsToExpiration) {
        this.secondsToExpiration = secondsToExpiration;
    }

    public Token getLeadToken() {
        return leadToken;
    }

    public void setLeadToken(Token leadToken) {
        this.leadToken = leadToken;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
