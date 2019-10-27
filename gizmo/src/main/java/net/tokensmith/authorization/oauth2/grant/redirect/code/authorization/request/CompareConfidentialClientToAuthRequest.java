package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request;

import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
