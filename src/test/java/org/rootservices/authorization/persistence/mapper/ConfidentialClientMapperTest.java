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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


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
    private LoadCodeClientWithScopes loadCodeClientWithScopes;

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

        // left off here!
        Client client = loadCodeClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        subject.insert(confidentialClient);

        ConfidentialClient actual = subject.getByClientUUID(client.getUuid());

        // confidential client
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUuid(), is(confidentialClient.getUuid()));
        assertThat(actual.getPassword(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        // client
        assertThat(actual.getClient(), is(notNullValue()));
        assertThat(actual.getClient().getUuid(), is(client.getUuid()));
        assertThat(actual.getClient().getRedirectURI(), is(client.getRedirectURI()));

        assertThat(actual.getClient().getResponseTypes(), is(notNullValue()));
        assertThat(actual.getClient().getResponseTypes().size(), is(1));
        assertThat(actual.getClient().getResponseTypes().get(0).getId(), is(notNullValue()));
        assertThat(actual.getClient().getResponseTypes().get(0).getName(), is("CODE"));
        assertThat(actual.getClient().getResponseTypes().get(0).getCreatedAt(), is(notNullValue()));
        assertThat(actual.getClient().getResponseTypes().get(0).getUpdatedAt(), is(notNullValue()));

        assertThat(actual.getClient().getCreatedAt(), is(notNullValue()));

        // scopes
        assertThat(actual.getClient().getScopes(), is(notNullValue()));
        assertThat(actual.getClient().getScopes().size(), is(1));
        assertThat(actual.getClient().getScopes().get(0).getUuid(), is(client.getScopes().get(0).getUuid()));
        assertThat(actual.getClient().getScopes().get(0).getName(), is("profile"));
    }
}
