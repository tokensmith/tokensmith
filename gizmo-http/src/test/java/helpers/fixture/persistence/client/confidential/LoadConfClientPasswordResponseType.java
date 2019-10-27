package helpers.fixture.persistence.client.confidential;

import helpers.fixture.EntityFactory;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 9/30/16.
 */
@Component
public class LoadConfClientPasswordResponseType extends LoadConfClient{

    @Autowired
    public LoadConfClientPasswordResponseType(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ConfidentialClientRepository confidentialClientRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, confidentialClientRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    public Client makeClient() throws URISyntaxException {
        return EntityFactory.makeClientWithPasswordResponseTypeAndScopes();
    }

    @Override
    public ConfidentialClient makeConfClient(Client client) {
        return EntityFactory.makeConfidentialClient(client);
    }
}
