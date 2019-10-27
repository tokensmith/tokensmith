package helper.fixture.persistence;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.repository.*;
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
