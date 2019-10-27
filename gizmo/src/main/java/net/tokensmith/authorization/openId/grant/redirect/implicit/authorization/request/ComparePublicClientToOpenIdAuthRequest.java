package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.CompareClientToOpenIdAuthRequest;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ClientRepository;
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
