package org.rootservices.authorization.openId.grant.token.request.entity;

import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;


public class OpenIdTokenAuthRequest extends OpenIdAuthRequest {
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
