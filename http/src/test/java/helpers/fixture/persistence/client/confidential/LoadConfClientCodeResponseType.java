package helpers.fixture.persistence.client.confidential;

/**
 * Created by tommackenzie on 6/5/15.
 */

import helpers.fixture.EntityFactory;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/16/15.
 *
 * sets up database with a client that has scopes.
 *
 * Client        ClientScopes            Scope
 * +uuid   --->  +client_uuid     /--->  +uuid
 *               +scope_uuid  ---/
 */
@Component
public class LoadConfClientCodeResponseType extends LoadConfClient {

    @Autowired
    public LoadConfClientCodeResponseType(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ConfidentialClientRepository confidentialClientRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, confidentialClientRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    public Client makeClient() throws URISyntaxException {
        return EntityFactory.makeClientWithCodeResponseTypeAndScopes();
    }

    @Override
    public ConfidentialClient makeConfClient(Client client) {
        return EntityFactory.makeConfidentialClient(client);
    }
}
