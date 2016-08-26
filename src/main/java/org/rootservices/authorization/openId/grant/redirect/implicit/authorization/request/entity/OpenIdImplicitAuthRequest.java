package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;

/**
 * Created by tommackenzie on 8/12/16.
 */
public class OpenIdImplicitAuthRequest extends BaseOpenIdAuthRequest {
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
