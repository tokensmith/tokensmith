package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;
import org.rootservices.authorization.parse.Parameter;

/**
 * Created by tommackenzie on 8/12/16.
 */
public class OpenIdImplicitAuthRequest extends BaseOpenIdAuthRequest {
    @Parameter(name = "nonce")
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
