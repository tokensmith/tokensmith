package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/16/16.
 */
@Component
public class ComparePublicClientToAuthRequest extends CompareClientToAuthRequest {
    @Autowired
    private ClientRepository clientRepository;

    public ComparePublicClientToAuthRequest() {
    }

    public ComparePublicClientToAuthRequest(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws RecordNotFoundException {
        return clientRepository.getById(clientId);
    }
}
