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
import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/15.
 */
@Component
public class LoadOpenIdClientWithScopes {
    private ClientRepository clientRepository;
    private ScopeRepository scopeRepository;
    private ClientScopesRepository clientScopesRepository;

    @Autowired
    public LoadOpenIdClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
    }

    public Client run() throws URISyntaxException {
        Client client = FixtureFactory.makeClientWithOpenIdScopes();
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
