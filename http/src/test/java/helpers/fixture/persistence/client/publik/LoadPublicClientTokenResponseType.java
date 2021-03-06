package helpers.fixture.persistence.client.publik;

import helpers.fixture.EntityFactory;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ClientResponseType;
import net.tokensmith.repository.entity.ClientScope;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ClientResponseTypeRepository;
import net.tokensmith.repository.repo.ClientScopesRepository;
import net.tokensmith.repository.repo.ResponseTypeRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/29/16.
 */
@Component
public class LoadPublicClientTokenResponseType {
    private ClientRepository clientRepository;
    private ScopeRepository scopeRepository;
    private ClientScopesRepository clientScopesRepository;
    private ResponseTypeRepository responseTypeRepository;
    private ClientResponseTypeRepository clientResponseTypeRepository;

    @Autowired
    public LoadPublicClientTokenResponseType(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
        this.responseTypeRepository = responseTypeRepository;
        this.clientResponseTypeRepository = clientResponseTypeRepository;
    }

    public Client run() throws URISyntaxException {
        Client client = EntityFactory.makeClientWithTokenResponseTypeAndScopes();
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
