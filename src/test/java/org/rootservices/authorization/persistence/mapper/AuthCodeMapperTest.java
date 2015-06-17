package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadClientWithScopes;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AuthCodeMapperTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ScopeRepository scopeRepository;
    @Autowired
    private ClientScopesRepository clientScopesRepository;
    @Autowired
    private ConfidentialClientRepository confidentialClientRepository;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    @Autowired
    private AccessRequestScopesRepository accessRequestScopesRepository;

    @Autowired
    private AuthCodeMapper subject;

    @Test
    public void insert() throws URISyntaxException {

        // prepare db for test.
        Client client = FixtureFactory.makeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner.getUuid(),
                client.getUuid(),
                null
        );
        accessRequestRepository.insert(accessRequest);
        // end prepare db for test.

        AuthCode authCode = FixtureFactory.makeAuthCode(resourceOwner.getUuid(), client.getUuid(), accessRequest);
        subject.insert(authCode);
    }

    @Test
    public void getByClientUUIDAndAuthCode() throws URISyntaxException {
        LoadClientWithScopes loadClientWithScopes = new LoadClientWithScopes(
                clientRepository,
                scopeRepository,
                clientScopesRepository
        );

        LoadConfidentialClientTokenReady loadConfidentialClientTokenReady = new LoadConfidentialClientTokenReady(
                loadClientWithScopes,
                confidentialClientRepository,
                resourceOwnerRepository,
                authCodeRepository,
                accessRequestRepository,
                accessRequestScopesRepository
        );
        AuthCode expected = loadConfidentialClientTokenReady.run();

        AuthCode actual = subject.getByClientUUIDAndAuthCode(expected.getClientUUID(), "authortization_code");

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());
        assertThat(actual.getAccessRequest()).isNotNull();
        assertThat(actual.getAccessRequest().getUuid()).isEqualTo(expected.getAccessRequest().getUuid());
        assertThat(actual.getAccessRequest().getScopes()).isNotNull();
        assertThat(actual.getAccessRequest().getScopes().size()).isEqualTo(1);
        assertThat(actual.getAccessRequest().getScopes().get(0).getName()).isEqualTo("profile");
    }
}
