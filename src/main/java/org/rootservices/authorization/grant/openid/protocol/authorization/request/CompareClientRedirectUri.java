package org.rootservices.authorization.grant.openid.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public interface CompareClientRedirectUri {
    boolean run(UUID clientId, URI redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException;
}
