package org.rootservices.authorization.openId.grant.redirect.token.request.entity;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdAuthRequest;


public class OpenIdTokenAuthRequest extends OpenIdAuthRequest {
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
