package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;

import java.net.URI;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public interface CompareClientRedirectUri {
    boolean run(UUID clientId, URI redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException;
}
