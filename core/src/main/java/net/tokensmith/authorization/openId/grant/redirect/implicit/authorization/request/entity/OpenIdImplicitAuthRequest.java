package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.entity;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;
import net.tokensmith.parser.Parameter;

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
