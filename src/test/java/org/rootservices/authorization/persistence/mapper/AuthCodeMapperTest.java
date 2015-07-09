package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AuthCodeMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;

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
                client.getUuid()
        );
        accessRequestRepository.insert(accessRequest);
        // end prepare db for test.

        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest);
        subject.insert(authCode);
    }

    @Test
    public void getByClientUUIDAndAuthCode() throws URISyntaxException {

        AuthCode expected = loadConfidentialClientTokenReady.run(true);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientUUIDAndAuthCode(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar).isNotNull();
        assertThat(ar.getUuid()).isEqualTo(expected.getAccessRequest().getUuid());
        assertThat(ar.getScopes()).isNotNull();
        assertThat(ar.getScopes().size()).isEqualTo(1);
        assertThat(ar.getScopes().get(0).getName()).isEqualTo("profile");
        assertThat(ar.getRedirectURI().isPresent()).isTrue();
        assertThat(ar.getRedirectURI().get().toString()).isEqualTo(FixtureFactory.SECURE_REDIRECT_URI);
    }

    @Test
    public void getByClientUUIDAndAuthCodeRedirectURIIsNotPresent() throws URISyntaxException {

        AuthCode expected = loadConfidentialClientTokenReady.run(false);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientUUIDAndAuthCode(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar).isNotNull();
        assertThat(ar.getUuid()).isEqualTo(expected.getAccessRequest().getUuid());
        assertThat(ar.getScopes()).isNotNull();
        assertThat(ar.getScopes().size()).isEqualTo(1);
        assertThat(ar.getScopes().get(0).getName()).isEqualTo("profile");
        assertThat(ar.getRedirectURI().isPresent()).isFalse();
    }
}
