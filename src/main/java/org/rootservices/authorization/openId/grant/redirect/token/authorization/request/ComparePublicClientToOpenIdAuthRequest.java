package org.rootservices.authorization.openId.grant.redirect.token.authorization.request;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.CompareClientToOpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 8/12/16.
 */
@Component
public class ComparePublicClientToOpenIdAuthRequest extends CompareClientToOpenIdAuthRequest {
    @Autowired
    private ClientRepository clientRepository;

    public ComparePublicClientToOpenIdAuthRequest() {
    }

    public ComparePublicClientToOpenIdAuthRequest(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    protected Client getClient(UUID clientId) throws RecordNotFoundException {
        return clientRepository.getById(clientId);
    }
}
