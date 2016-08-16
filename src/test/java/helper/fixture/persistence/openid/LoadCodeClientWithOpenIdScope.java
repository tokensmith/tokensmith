package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 1/21/16.
 */
@Component
public class LoadCodeClientWithOpenIdScope extends LoadCodeClientWithScopes {

    @Autowired
    public LoadCodeClientWithOpenIdScope(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeCodeClientWithOpenIdScopes();
    }
}
