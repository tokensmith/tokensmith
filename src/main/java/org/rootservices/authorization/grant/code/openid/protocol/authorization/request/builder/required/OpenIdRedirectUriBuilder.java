package org.rootservices.authorization.grant.code.openid.protocol.authorization.request.builder.required;

import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.RedirectUriException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface OpenIdRedirectUriBuilder {
    URI build(List<String> redirectUris) throws RedirectUriException;
}
