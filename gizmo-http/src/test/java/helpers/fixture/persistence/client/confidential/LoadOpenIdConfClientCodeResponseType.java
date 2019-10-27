package helpers.fixture.persistence.client.confidential;

import helpers.fixture.EntityFactory;
import net.tokensmith.authorization.persistence.entity.*;
import net.tokensmith.authorization.persistence.repository.*;
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
