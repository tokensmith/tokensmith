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
 * Created by tommackenzie on 5/16/15.
 *
 * sets up database with a client that has scopes and the response type, code.
 *
 * Client        ClientScopes            Scope
 * +uuid   --->  +client_uuid     /--->  +uuid
 *               +scope_uuid  ---/
 */
@Component
public class LoadCodeClientWithScopes extends LoadClientWithScopes {

    @Autowired
    public LoadCodeClientWithScopes(ClientRepository clientRepository, ScopeRepository scopeRepository, ClientScopesRepository clientScopesRepository, ResponseTypeRepository responseTypeRepository, ClientResponseTypeRepository clientResponseTypeRepository) {
        super(clientRepository, scopeRepository, clientScopesRepository, responseTypeRepository, clientResponseTypeRepository);
    }

    protected Client makeClientWithScopes() throws URISyntaxException {
        return FixtureFactory.makeCodeClientWithScopes();
    }
}
