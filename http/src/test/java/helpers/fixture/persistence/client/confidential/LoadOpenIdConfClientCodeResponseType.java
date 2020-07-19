package helpers.fixture.persistence.client.confidential;

import helpers.fixture.EntityFactory;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ClientResponseTypeRepository;
import net.tokensmith.repository.repo.ClientScopesRepository;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
import net.tokensmith.repository.repo.ResponseTypeRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 10/13/15.
 */
@Component
public class LoadOpenIdConfClientCodeResponseType extends LoadConfClient {

    @Autowired
    public LoadOpenIdConfClientCodeResponseType(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ConfidentialClientRepository confidentialClientRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, confidentialClientRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    public Client makeClient() throws URISyntaxException {
        return EntityFactory.makeOpenIdCodeClientWithScopes();
    }

    @Override
    public ConfidentialClient makeConfClient(Client client) {
        return EntityFactory.makeConfidentialClient(client);
    }
}
