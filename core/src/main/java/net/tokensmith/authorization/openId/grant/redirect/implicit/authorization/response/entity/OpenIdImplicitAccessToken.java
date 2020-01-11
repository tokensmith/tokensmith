package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity;

import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/23/16.
 */
public class OpenIdImplicitAccessToken {
    private URI redirectUri;
    private String accessToken;
    private TokenType tokenType;
    private String idToken;
    private Long expiresIn;
    private Optional<String> scope;
    private Optional<String> state;

    private List<UUID> audience;
    private Long issuedAt;
    private Long expirationTime;
    private Long authTime;

    public OpenIdImplicitAccessToken() {
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Optional<String> getScope() {
        return scope;
    }

    public void setScope(Optional<String> scope) {
        this.scope = scope;
    }

    public Optional<String> getState() {
        return state;
    }

    public void setState(Optional<String> state) {
        this.state = state;
    }

    public List<UUID> getAudience() {
        return audience;
    }

    public void setAudience(List<UUID> audience) {
        this.audience = audience;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Long authTime) {
        this.authTime = authTime;
    }
}
