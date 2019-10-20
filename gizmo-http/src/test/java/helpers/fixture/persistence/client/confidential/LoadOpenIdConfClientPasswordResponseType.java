package helpers.fixture.persistence.client.confidential;

import helpers.fixture.EntityFactory;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 9/30/16.
 */
@Component
public class LoadOpenIdConfClientPasswordResponseType extends LoadConfClient {

    @Autowired
    public LoadOpenIdConfClientPasswordResponseType(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ConfidentialClientRepository confidentialClientRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, confidentialClientRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    public Client makeClient() throws URISyntaxException {
        return EntityFactory.makeOpenIdClientWithPasswordResponseTypeAndScopes();
    }

    @Override
    public ConfidentialClient makeConfClient(Client client) {
        return EntityFactory.makeConfidentialClient(client);
    }
}
