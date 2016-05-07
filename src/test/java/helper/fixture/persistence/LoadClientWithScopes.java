package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/4/16.
 */
public abstract class LoadClientWithScopes {

    protected ClientRepository clientRepository;
    protected ScopeRepository scopeRepository;
    protected ClientScopesRepository clientScopesRepository;

    public LoadClientWithScopes() {}

    public LoadClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
    }

    public Client run() throws URISyntaxException {
        Client client = makeClientWithScopes();
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

    abstract protected Client makeClientWithScopes() throws URISyntaxException;
}
