package org.rootservices.authorization.oauth2.grant.token.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/21/16.
 */
public class TokenClaims {
    private String issuer;
    private List<String> audience;
    private Long issuedAt;
    private Long expirationTime; // 130584847 optional
    private Long authTime; // 130584847 optional

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
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
