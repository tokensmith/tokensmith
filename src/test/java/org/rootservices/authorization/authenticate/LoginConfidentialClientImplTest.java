package org.rootservices.authorization.authenticate;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ClientScopesRepository;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Created by tommackenzie on 5/25/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public class LoginConfidentialClientImplTest {

    private LoadClientWithScopes loadClientWithScopes;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private ClientScopesRepository clientScopesRepository;
    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    private LoginConfidentialClient subject;

    @Before
    public void setUp(){
        loadClientWithScopes = new LoadClientWithScopes(
                clientRepository, scopeRepository, clientScopesRepository
        );
    }

    public ConfidentialClient prepareDatabaseForTest() throws URISyntaxException {
        Client client = loadClientWithScopes.run();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);
        confidentialClientRepository.insert(confidentialClient);
        return confidentialClient;
    }

    @Test
    public void run() throws URISyntaxException, UnauthorizedException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = subject.run(expected.getClient().getUuid(), "password");

        // confidential client
        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());
        assertThat(actual.getPassword()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();

        // client
        assertThat(actual.getClient()).isNotNull();
        assertThat(actual.getClient().getUuid()).isEqualTo(expected.getClient().getUuid());
        assertThat(actual.getClient().getRedirectURI()).isEqualTo(expected.getClient().getRedirectURI());
        assertThat(actual.getClient().getResponseType()).isEqualTo(expected.getClient().getResponseType());
        assertThat(actual.getClient().getCreatedAt()).isNotNull();

        // scopes
        assertThat(actual.getClient().getScopes()).isNotNull();
        assertThat(actual.getClient().getScopes().size()).isEqualTo(1);
        assertThat(actual.getClient().getScopes().get(0).getUuid()).isEqualTo(
                expected.getClient().getScopes().get(0).getUuid()
        );
        assertThat(actual.getClient().getScopes().get(0).getName()).isEqualTo("profile");
    }

    @Test
    public void confidentialClientNotFound() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(UUID.randomUUID(), "password");
        } catch (UnauthorizedException e) {
            assertThat(e.getDomainCause()).isInstanceOf(RecordNotFoundException.class);
            assertThat(e.getCode()).isEqualTo(ErrorCode.CLIENT_NOT_FOUND.getCode());
        }
        assertThat(actual).isNull();
    }

    @Test
    public void passwordIncorrect() throws URISyntaxException {
        ConfidentialClient expected = prepareDatabaseForTest();
        ConfidentialClient actual = null;

        try {
            actual = subject.run(expected.getClient().getUuid(), "passwordThatDoesNotMatch");
        } catch (UnauthorizedException e) {
            assertThat(e.getCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getCode());
        }
        assertThat(actual).isNull();
    }
}