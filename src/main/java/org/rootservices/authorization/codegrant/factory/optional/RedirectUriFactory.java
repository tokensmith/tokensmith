package org.rootservices.authorization.codegrant.factory.optional;

import org.rootservices.authorization.codegrant.factory.exception.RedirectUriException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface RedirectUriFactory {
    public Optional<URI> makeRedirectUri(List<String> redirectUris) throws RedirectUriException;
}
