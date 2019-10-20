package org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.context;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URI;
import java.util.UUID;

/**
 * Created by tommackenzie on 8/11/16.
 */
public abstract class GetOpenIdClientRedirectUri {

    public boolean run(UUID clientId, URI redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException {

        Client client;
        try {
            client = getClient(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( !client.getRedirectURI().equals(redirectURI)) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return true;
    }

    protected abstract Client getClient(UUID clientId) throws RecordNotFoundException;
}
