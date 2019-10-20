package helpers.fixture.persistence.client.publik;

import helpers.fixture.EntityFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/29/16.
 */
public abstract class LoadPublicClient {
    private ClientRepository clientRepository;
    private ScopeRepository scopeRepository;
    private ClientScopesRepository clientScopesRepository;
    private ResponseTypeRepository responseTypeRepository;
    private ClientResponseTypeRepository clientResponseTypeRepository;

    @Autowired
    public LoadPublicClient(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
        this.responseTypeRepository = responseTypeRepository;
        this.clientResponseTypeRepository = clientResponseTypeRepository;
    }

    public abstract Client makeClient() throws URISyntaxException;

    public Client run() throws URISyntaxException {
        Client client = makeClient();
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

}
