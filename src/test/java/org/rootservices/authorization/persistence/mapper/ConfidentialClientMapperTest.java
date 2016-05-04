package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 5/24/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class ConfidentialClientMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private ClientScopesRepository clientScopesRepository;

    @Autowired
    private ConfidentialClientMapper subject;

    @Test
    public void insert() throws URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        clientRepository.insert(client);

        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        subject.insert(confidentialClient);
    }

    @Test
    public void getByClientUUID() throws URISyntaxException {
        LoadCodeClientWithScopes loadClientWithScopes = new LoadCodeClientWithScopes(
                clientRepository, scopeRepository, clientScopesRepository
        );
        Client client = loadClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        subject.insert(confidentialClient);

        ConfidentialClient actual = subject.getByClientUUID(client.getUuid());

        // confidential client
        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(confidentialClient.getUuid());
        assertThat(actual.getPassword()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();

        // client
        assertThat(actual.getClient()).isNotNull();
        assertThat(actual.getClient().getUuid()).isEqualTo(client.getUuid());
        assertThat(actual.getClient().getRedirectURI()).isEqualTo(client.getRedirectURI());
        assertThat(actual.getClient().getResponseType()).isEqualTo(client.getResponseType());
        assertThat(actual.getClient().getCreatedAt()).isNotNull();

        // scopes
        assertThat(actual.getClient().getScopes()).isNotNull();
        assertThat(actual.getClient().getScopes().size()).isEqualTo(1);
        assertThat(actual.getClient().getScopes().get(0).getUuid()).isEqualTo(
                client.getScopes().get(0).getUuid()
        );
        assertThat(actual.getClient().getScopes().get(0).getName()).isEqualTo("profile");
    }
}
