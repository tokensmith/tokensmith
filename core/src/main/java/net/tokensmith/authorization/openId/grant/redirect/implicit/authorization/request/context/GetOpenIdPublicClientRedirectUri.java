package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.context;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 8/12/16.
 */
@Component
public class GetOpenIdPublicClientRedirectUri extends GetOpenIdClientRedirectUri {
    @Autowired
    private ClientRepository clientRepository;

    public GetOpenIdPublicClientRedirectUri() {
    }

    public GetOpenIdPublicClientRedirectUri(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    protected Client getClient(UUID clientId) throws RecordNotFoundException {
        return clientRepository.getById(clientId);
    }
}
