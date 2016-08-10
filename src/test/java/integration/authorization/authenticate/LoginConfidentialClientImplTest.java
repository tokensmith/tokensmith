package integration.authorization.authenticate;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 5/25/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public class LoginConfidentialClientImplTest {

    @Autowired
    private LoadCodeClientWithScopes loadCodeClientWithScopes;
    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    private LoginConfidentialClient subject;

    public ConfidentialClient prepareDatabaseForTest() throws URISyntaxException {
        Client client = loadCodeClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);
        return confidentialClient;
    }

    @Test
    public void run() throws URISyntaxException, UnauthorizedException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = subject.run(expected.getClient().getUuid(), "password");

        // confidential client
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUuid(), is(expected.getUuid()));
        assertThat(actual.getPassword(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        // client
        assertThat(actual.getClient(), is(notNullValue()));
        assertThat(actual.getClient().getUuid(), is(expected.getClient().getUuid()));
        assertThat(actual.getClient().getRedirectURI(), is(expected.getClient().getRedirectURI()));

        // response types
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
        assertThat(actual.getClient().getScopes().get(0).getUuid(), is(expected.getClient().getScopes().get(0).getUuid()));
        assertThat(actual.getClient().getScopes().get(0).getName(), is("profile"));
    }

    @Test
    public void confidentialClientNotFound() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(UUID.randomUUID(), "password");
        } catch (UnauthorizedException e) {
            assertThat(e.getDomainCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        }
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void passwordIncorrect() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(expected.getClient().getUuid(), "passwordThatDoesNotMatch");
        } catch (UnauthorizedException e) {
            assertThat(e.getCode(), is(ErrorCode.PASSWORD_MISMATCH.getCode()));
        }
        assertThat(actual, is(nullValue()));
    }
}