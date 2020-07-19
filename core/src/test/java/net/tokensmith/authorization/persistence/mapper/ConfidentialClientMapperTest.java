package net.tokensmith.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.TestAppConfig;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.repo.ClientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 5/24/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestAppConfig.class, loader= AnnotationConfigContextLoader.class)
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

        ConfidentialClient actual = subject.getByClientId(client.getId());

        // confidential client
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(confidentialClient.getId()));
        assertThat(actual.getPassword(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        // client
        assertThat(actual.getClient(), is(notNullValue()));
        assertThat(actual.getClient().getId(), is(client.getId()));
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
        assertThat(actual.getClient().getScopes().get(0).getId(), is(client.getScopes().get(0).getId()));
        assertThat(actual.getClient().getScopes().get(0).getName(), is("profile"));
    }
}
