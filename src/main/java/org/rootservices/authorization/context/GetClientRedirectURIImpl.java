package org.rootservices.authorization.context;

import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

/**
 * Created by tommackenzie on 12/10/14.
 */
@Component
public class GetClientRedirectURIImpl implements GetClientRedirectURI {

    @Autowired
    private ClientRepository clientRepository;

    public URI run(UUID uuid) throws RecordNotFoundException {
        Client client = clientRepository.getByUUID(uuid);
        return client.getRedirectURI();
    }
}
