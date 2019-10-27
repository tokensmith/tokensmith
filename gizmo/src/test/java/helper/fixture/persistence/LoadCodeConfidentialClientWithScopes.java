package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/8/16.
 */
@Component
public class LoadCodeConfidentialClientWithScopes {

    private LoadCodeClientWithScopes loadCodeClientWithScopes;
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public LoadCodeConfidentialClientWithScopes(LoadCodeClientWithScopes loadCodeClientWithScopes, ConfidentialClientRepository confidentialClientRepository) {
        this.loadCodeClientWithScopes = loadCodeClientWithScopes;
        this.confidentialClientRepository = confidentialClientRepository;
    }

    public ConfidentialClient run() throws URISyntaxException {
        Client client = loadCodeClientWithScopes.run();

        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);

        return confidentialClient;
    }
}
