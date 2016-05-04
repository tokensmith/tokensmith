package org.rootservices.authorization.persistence.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
public class Token {
    private UUID uuid;
    private byte[] token;
    private boolean revoked;
    private GrantType grantType;
    private List<TokenScope> tokenScopes;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;


    public Token() {}

    public Token(UUID uuid, byte[] token, OffsetDateTime expiresAt) {
        this.uuid = uuid;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
