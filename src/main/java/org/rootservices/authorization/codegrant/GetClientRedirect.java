package org.rootservices.authorization.codegrant;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public interface GetClientRedirect {
    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException;
}
