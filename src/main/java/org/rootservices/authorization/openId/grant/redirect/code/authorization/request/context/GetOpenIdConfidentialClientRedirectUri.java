package org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/8/15.
 *
 */
@Component
public class GetOpenIdConfidentialClientRedirectUri extends GetOpenIdClientRedirectUri {

    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    public GetOpenIdConfidentialClientRedirectUri() {}

    public GetOpenIdConfidentialClientRedirectUri(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    protected Client getClient(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientRepository.getByClientId(clientId);
        return confidentialClient.getClient();
    }
}
