package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
@Component
public class GetConfidentialClientRedirectUriImpl extends GetClientRedirectUri {

    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    public GetConfidentialClientRedirectUriImpl() {
    }

    public GetConfidentialClientRedirectUriImpl(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientRepository.getByClientId(clientId);
        return confidentialClient.getClient();
    }
}
