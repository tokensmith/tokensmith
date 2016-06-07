package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.context;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
public abstract class GetClientRedirectUri {

    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException {

        Client client;
        try {
            client = getClient(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( redirectMismatch(redirectURI, client.getRedirectURI())) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return client.getRedirectURI();
    }

    public abstract Client getClient(UUID clientId) throws RecordNotFoundException;

    /*
    returns true if the redirect does not match client's redirect, otherwise
    returns false
     */
    private boolean redirectMismatch(Optional<URI> redirect, URI clientRedirect) {
        boolean matches = false;
        if ( redirect.isPresent()) {
            matches = !clientRedirect.equals(redirect.get());
        }
        return matches;
    }
}
