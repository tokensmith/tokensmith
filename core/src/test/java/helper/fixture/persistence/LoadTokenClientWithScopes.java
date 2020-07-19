package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ClientResponseTypeRepository;
import net.tokensmith.repository.repo.ClientScopesRepository;
import net.tokensmith.repository.repo.ResponseTypeRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 5/4/16.
 */
@Component
public class LoadTokenClientWithScopes extends LoadClientWithScopes {

    @Autowired
    public LoadTokenClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    @Override
    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeTokenClientWithScopes();
    }
}
