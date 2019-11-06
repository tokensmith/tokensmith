package net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.context;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
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
