package org.rootservices.authorization.persistence.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ClientScopesMapperTests {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ClientScopesMapper subject;

    private UUID insertClient() throws URISyntaxException {
        Client client = new Client(
                UUID.randomUUID(),
                ResponseType.CODE,
                new URI("https://rootservices.org/continue")
        );
        clientRepository.insert(client);
        return client.getUuid();
    }

    private UUID insertScope() {
        Scope scope = new Scope(
                UUID.randomUUID(),
                "profile"
        );
        scopeRepository.insert(scope);
        return scope.getUuid();
    }

    @Test
    public void insert() throws URISyntaxException {
        UUID clientUUID = insertClient();
        UUID scopeUUID = insertScope();

        ClientScope clientScope = new ClientScope(
                UUID.randomUUID(), clientUUID, scopeUUID
        );

        subject.insert(clientScope);
    }
}
