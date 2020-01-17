package net.tokensmith.repository.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
public class Token {
    private UUID id;
    private String token;
    private boolean revoked;
    private GrantType grantType;
    private UUID clientId;
    private List<TokenScope> tokenScopes;
    private List<Client> audience;
    private Optional<String> nonce;
    private Long secondsToExpiration;
    private Token leadToken; // optional
    private OffsetDateTime leadAuthTime;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public Token() {}

    public Token(UUID id, String token, OffsetDateTime expiresAt) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
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

    public List<Client> getAudience() {
        return audience;
    }

    public void setAudience(List<Client> audience) {
        this.audience = audience;
    }

    public Optional<String> getNonce() {
        return nonce;
    }

    public void setNonce(Optional<String> nonce) {
        this.nonce = nonce;
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

    public OffsetDateTime getLeadAuthTime() {
        return leadAuthTime;
    }

    public void setLeadAuthTime(OffsetDateTime leadAuthTime) {
        this.leadAuthTime = leadAuthTime;
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
