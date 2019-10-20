package helpers.fixture.persistence.client.confidential;

import helpers.fixture.EntityFactory;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/29/16.
 */
@Component
public abstract class LoadConfClient {

    private ClientRepository clientRepository;
    private ScopeRepository scopeRepository;
    private ClientScopesRepository clientScopesRepository;
    private ConfidentialClientRepository confidentialClientRepository;
    private ResponseTypeRepository responseTypeRepository;
    private ClientResponseTypeRepository clientResponseTypeRepository;

    @Autowired
    public LoadConfClient(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ConfidentialClientRepository confidentialClientRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.clientScopesRepository = clientScopesRepository;
        this.confidentialClientRepository = confidentialClientRepository;
        this.responseTypeRepository = responseTypeRepository;
        this.clientResponseTypeRepository = clientResponseTypeRepository;
    }

    public abstract Client makeClient() throws URISyntaxException;
    public abstract ConfidentialClient makeConfClient(Client client);

    public ConfidentialClient run() throws URISyntaxException {
        Client client = makeClient();
        clientRepository.insert(client);

        ConfidentialClient confidentialClient = makeConfClient(client);
        confidentialClientRepository.insert(confidentialClient);

        List<Scope> scopesForClient = new ArrayList<>();
        for (Scope scope: client.getScopes()) {
            Scope scopeForClient;
            try {
                scopeForClient = scopeRepository.findByName(scope.getName());
            } catch (RecordNotFoundException e) {
                throw new RuntimeException("failed to set up client");
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
                throw new RuntimeException("failed to set up client");
            }

            ClientResponseType clientResponseType = new ClientResponseType(UUID.randomUUID(), rt, client);
            clientResponseTypeRepository.insert(clientResponseType);
        }

        return confidentialClient;
    }
}
