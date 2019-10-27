package net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.request;

import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/16/16.
 */
@Component
public class ComparePublicClientToAuthRequest extends CompareClientToAuthRequest {

    private ClientRepository clientRepository;

    @Autowired
    public ComparePublicClientToAuthRequest(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getClient(UUID clientId) throws RecordNotFoundException {
        return clientRepository.getById(clientId);
    }
}
