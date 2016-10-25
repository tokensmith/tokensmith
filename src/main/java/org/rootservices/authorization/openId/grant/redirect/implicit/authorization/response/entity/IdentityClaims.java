package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity;

import java.util.List;

/**
 * Created by tommackenzie on 10/24/16.
 */
public class IdentityClaims {
    private String issuer;
    private List<String> audience;
    private Long issuedAt;

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
}
