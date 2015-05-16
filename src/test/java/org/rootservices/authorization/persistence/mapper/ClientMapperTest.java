package org.rootservices.authorization.persistence.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 11/15/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
public class ClientMapperTest {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ClientScopesRepository clientScopesRepository;

    @Autowired
    private ClientMapper subject;

    public Client insertClient() throws URISyntaxException{
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(uuid, rt, redirectURI);

        subject.insert(client);
        return client;
    }

    private UUID insertScope() {
        Scope scope = new Scope(
                UUID.randomUUID(),
                "profile"
        );
        scopeRepository.insert(scope);
        return scope.getUuid();
    }

    private void insertClientScope(UUID clientUUID, UUID scopeUUID) {
        ClientScope clientScope = new ClientScope(
                UUID.randomUUID(), clientUUID, scopeUUID
        );
        clientScopesRepository.insert(clientScope);
    }

    @Test
    @Transactional
    public void insert() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectURI = new URI("https://rootservices.org");
        Client client = new Client(uuid, rt, redirectURI);

        subject.insert(client);
    }

    @Test
    @Transactional
    public void getByUUID() throws URISyntaxException {
        Client expectedClient = insertClient();
        UUID scopeUUId = insertScope();
        insertClientScope(expectedClient.getUuid(), scopeUUId);

        Client actualClient = subject.getByUUID(expectedClient.getUuid());

        assertThat(actualClient.getUuid()).isEqualTo(expectedClient.getUuid());
        assertThat(actualClient.getResponseType()).isEqualTo(expectedClient.getResponseType());
        assertThat(actualClient.getCreatedAt()).isNotNull();
        assertThat(actualClient.getScopes().size()).isEqualTo(1);
        assertThat(actualClient.getScopes().get(0).getUuid()).isEqualTo(scopeUUId);
        assertThat(actualClient.getScopes().get(0).getName()).isEqualTo("profile");
    }

    @Test
    @Transactional
    public void getByUUIDNotFound() {
        Client actualClient = subject.getByUUID(UUID.randomUUID());

        assertThat(actualClient).isEqualTo(null);
    }
}
