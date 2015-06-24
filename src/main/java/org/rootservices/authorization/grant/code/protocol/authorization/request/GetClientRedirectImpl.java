package org.rootservices.authorization.grant.code.protocol.authorization.request;

import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 *
 * TODO: rename this class, its doesn not describe what it does.
 */
@Component
public class GetClientRedirectImpl implements GetClientRedirect {

    @Autowired
    private ClientRepository clientRepository;

    public GetClientRedirectImpl() {}

    public GetClientRedirectImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException {

        Client client;
        try {
            client = clientRepository.getByUUID(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( redirectMismatch(redirectURI, client.getRedirectURI())) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return client.getRedirectURI();
    }

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
