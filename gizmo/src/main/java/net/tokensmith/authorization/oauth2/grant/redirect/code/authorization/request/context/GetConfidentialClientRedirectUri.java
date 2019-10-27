package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request.context;

import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
@Component
public class GetConfidentialClientRedirectUri extends GetClientRedirectUri {

    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    public GetConfidentialClientRedirectUri() {
    }

    public GetConfidentialClientRedirectUri(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws RecordNotFoundException {
        ConfidentialClient confidentialClient = confidentialClientRepository.getByClientId(clientId);
        return confidentialClient.getClient();
    }
}
