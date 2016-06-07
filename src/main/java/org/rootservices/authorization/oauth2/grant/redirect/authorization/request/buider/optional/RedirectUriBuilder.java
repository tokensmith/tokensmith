package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.RedirectUriException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RedirectUriBuilder {
    Optional<URI> makeRedirectUri(List<String> redirectUris) throws RedirectUriException;
}
