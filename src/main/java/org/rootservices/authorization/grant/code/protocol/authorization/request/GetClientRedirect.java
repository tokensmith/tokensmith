package org.rootservices.authorization.grant.code.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public interface GetClientRedirect {
    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException;
}
