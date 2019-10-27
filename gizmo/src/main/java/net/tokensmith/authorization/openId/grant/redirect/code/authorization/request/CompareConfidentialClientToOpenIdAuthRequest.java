package net.tokensmith.authorization.openId.grant.redirect.code.authorization.request;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.CompareClientToOpenIdAuthRequest;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 9/30/15.
 */
@Component
public class CompareConfidentialClientToOpenIdAuthRequest extends CompareClientToOpenIdAuthRequest {
    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    public CompareConfidentialClientToOpenIdAuthRequest() {
    }

    public CompareConfidentialClientToOpenIdAuthRequest(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    protected Client getClient(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientRepository.getByClientId(clientId);
        return confidentialClient.getClient();
    }
}
