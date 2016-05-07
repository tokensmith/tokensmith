package org.rootservices.authorization.openId.grant.code.authorization.request.builder.required;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.RedirectUriException;

import java.net.URI;
import java.util.List;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface OpenIdRedirectUriBuilder {
    URI build(List<String> redirectUris) throws RedirectUriException;
}
