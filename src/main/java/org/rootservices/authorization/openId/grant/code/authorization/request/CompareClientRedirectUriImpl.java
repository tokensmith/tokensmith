package org.rootservices.authorization.openId.grant.code.authorization.request;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/8/15.
 *
 */
@Component
public class CompareClientRedirectUriImpl implements CompareClientRedirectUri {

    private ClientRepository clientRepository;

    public CompareClientRedirectUriImpl() {}

    @Autowired
    public CompareClientRedirectUriImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean run(UUID clientId, URI redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException {

        Client client;
        try {
            client = clientRepository.getByUUID(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( !client.getRedirectURI().equals(redirectURI)) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return true;
    }
}
