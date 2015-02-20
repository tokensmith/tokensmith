package org.rootservices.authorization.codegrant;

import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
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

        if ( redirectEqualsClientRedirect(redirectURI, client.getRedirectURI())) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return client.getRedirectURI();
    }

    private boolean redirectEqualsClientRedirect(Optional<URI> redirectA, URI redirectB) {
        return (redirectA.isPresent() && !redirectA.get().equals(redirectB));
    }
}
