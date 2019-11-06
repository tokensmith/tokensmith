package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/9/16.
 */
@Component
public class LoadCodeConfidentialClientWithOpenIdScope {
    private LoadCodeClientWithOpenIdScope loadClientWithOpenIdScope;
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public LoadCodeConfidentialClientWithOpenIdScope(LoadCodeClientWithOpenIdScope loadClientWithOpenIdScope, ConfidentialClientRepository confidentialClientRepository) {
        this.loadClientWithOpenIdScope = loadClientWithOpenIdScope;
        this.confidentialClientRepository = confidentialClientRepository;
    }

    public ConfidentialClient run() throws URISyntaxException {
        Client client = loadClientWithOpenIdScope.run();

        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);

        return confidentialClient;
    }
}
