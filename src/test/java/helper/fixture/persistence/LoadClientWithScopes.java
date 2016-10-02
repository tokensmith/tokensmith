package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

        List<Scope> scopesForClient = new ArrayList<>();
        for (Scope scope: client.getScopes()) {
            Scope scopeForClient;
            try {
                scopeForClient = scopeRepository.findByName(scope.getName());
            } catch (RecordNotFoundException e) {
                continue;
            }
            scopesForClient.add(scopeForClient);

            ClientScope clientScope = new ClientScope(
                    UUID.randomUUID(), client.getId(), scopeForClient.getId()
            );
            clientScopesRepository.insert(clientScope);
        }
        client.setScopes(scopesForClient);

        List<ResponseType> responseTypesForClient = new ArrayList<>();
        for(ResponseType responseType: client.getResponseTypes()) {
            ResponseType rt;
            try {
                rt = responseTypeRepository.getByName(responseType.getName());
            } catch (RecordNotFoundException e) {
                continue;
            }
            responseTypesForClient.add(rt);

            ClientResponseType clientResponseType = new ClientResponseType(UUID.randomUUID(), rt, client);
            clientResponseTypeRepository.insert(clientResponseType);
        }
        client.setResponseTypes(responseTypesForClient);

        return client;
    }

    abstract protected Client makeClientWithScopes() throws URISyntaxException;
}
