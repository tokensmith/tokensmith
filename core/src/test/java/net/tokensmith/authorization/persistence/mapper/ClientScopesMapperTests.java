package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.TestAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ClientScope;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
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
                new URI("https://rootservices.org/continue")
        );
        clientRepository.insert(client);
        return client.getId();
    }

    private UUID insertScope() throws Exception {

        Scope scope = null;
        try {
            scope = scopeRepository.findByName("profile");
        } catch (RecordNotFoundException e) {
            throw new Exception("profile scope should have been seeded in migrations.");
        }
        return scope.getId();
    }

    @Test
    public void insert() throws Exception {
        UUID clientUUID = insertClient();
        UUID scopeUUID = insertScope();

        ClientScope clientScope = new ClientScope(
                UUID.randomUUID(), clientUUID, scopeUUID
        );

        subject.insert(clientScope);
    }
}
