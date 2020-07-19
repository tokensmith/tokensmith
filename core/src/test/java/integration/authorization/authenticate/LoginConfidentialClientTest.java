package integration.authorization.authenticate;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadCodeClientWithScopes;
import net.tokensmith.authorization.authenticate.LoginConfidentialClient;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ConfidentialClientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class LoginConfidentialClientTest {

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
        ConfidentialClient actual = subject.run(expected.getClient().getId(), "password");

        // confidential client
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getPassword(), is(notNullValue()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        // client
        assertThat(actual.getClient(), is(notNullValue()));
        assertThat(actual.getClient().getId(), is(expected.getClient().getId()));
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
        assertThat(actual.getClient().getScopes().get(0).getId(), is(expected.getClient().getScopes().get(0).getId()));
        assertThat(actual.getClient().getScopes().get(0).getName(), is("profile"));
    }

    @Test
    public void confidentialClientNotFound() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(UUID.randomUUID(), "password");
        } catch (UnauthorizedException e) {
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        }
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void passwordIncorrect() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(expected.getClient().getId(), "passwordThatDoesNotMatch");
        } catch (UnauthorizedException e) {
            assertThat(e.getCode(), is(ErrorCode.PASSWORD_MISMATCH.getCode()));
        }
        assertThat(actual, is(nullValue()));
    }
}