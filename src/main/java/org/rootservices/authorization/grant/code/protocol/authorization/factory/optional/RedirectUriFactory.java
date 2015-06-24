package org.rootservices.authorization.grant.code.protocol.authorization.factory.optional;

import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.RedirectUriException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RedirectUriFactory {
    public Optional<URI> makeRedirectUri(List<String> redirectUris) throws RedirectUriException;
}
