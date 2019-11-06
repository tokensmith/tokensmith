package helper.fixture.persistence.openid;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 8/12/16.
 */
@Component
public class LoadTokenClientWithOpenIdScope extends LoadClientWithScopes {

    @Autowired
    public LoadTokenClientWithOpenIdScope(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeTokenClientWithOpenIdScopes();
    }
}
