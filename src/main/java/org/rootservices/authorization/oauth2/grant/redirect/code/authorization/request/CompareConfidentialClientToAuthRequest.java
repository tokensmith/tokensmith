package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/19/14.
 */
@Component
public class CompareConfidentialClientToAuthRequest extends CompareClientToAuthRequest {

    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    public CompareConfidentialClientToAuthRequest() {}

    public CompareConfidentialClientToAuthRequest(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientRepository.getByClientId(clientId);
        return confidentialClient.getClient();
    }
}
