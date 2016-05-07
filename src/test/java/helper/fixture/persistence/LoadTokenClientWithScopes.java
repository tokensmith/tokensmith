package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/4/16.
 */
@Component
public class LoadTokenClientWithScopes extends LoadClientWithScopes {

    @Autowired
    public LoadTokenClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository);
    }

    @Override
    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeTokenClientWithScopes();
    }
}
