package net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.context;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;


public abstract class GetClientRedirectUri {

    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformResourceOwnerException {

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
