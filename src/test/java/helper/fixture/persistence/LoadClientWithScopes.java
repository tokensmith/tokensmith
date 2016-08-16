package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
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
    protected ResponseTypeRepository responseTypeRepository;
    protected ClientResponseTypeRepository clientResponseTypeRepository;

    public LoadClientWithScopes() {}

    public LoadClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
        this.responseTypeRepository = responseTypeRepository;
        this.clientResponseTypeRepository = clientResponseTypeRepository;
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

        for(ResponseType responseType: client.getResponseTypes()) {
            ResponseType rt;
            try {
                rt = responseTypeRepository.getByName(responseType.getName());
            } catch (RecordNotFoundException e) {
                continue;
            }

            ClientResponseType clientResponseType = new ClientResponseType(UUID.randomUUID(), rt, client);
            clientResponseTypeRepository.insert(clientResponseType);
        }

        return client;
    }

    abstract protected Client makeClientWithScopes() throws URISyntaxException;
}
