package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 1/21/16.
 */
@Component
public class LoadCodeClientWithOpenIdScope extends LoadCodeClientWithScopes {

    @Autowired
    public LoadCodeClientWithOpenIdScope(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository);
    }

    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeCodeClientWithOpenIdScopes();
    }
}
