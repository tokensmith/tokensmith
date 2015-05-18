package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public class LoadClientWithScopes {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private ClientScopesRepository clientScopesRepository;

    public LoadClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
    }

    public Client run() throws URISyntaxException {
        Client client = FixtureFactory.makeClientWithScopes();
        clientRepository.insert(client);

        for (Scope scope: client.getScopes()) {
            scopeRepository.insert(scope);
            ClientScope clientScope = new ClientScope(
                    UUID.randomUUID(), client.getUuid(), scope.getUuid()
            );
            clientScopesRepository.insert(clientScope);
        }
        return client;
    }
}
