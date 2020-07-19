package helpers.fixture.persistence.client.confidential;

/**
 * Created by tommackenzie on 6/5/15.
 */

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
