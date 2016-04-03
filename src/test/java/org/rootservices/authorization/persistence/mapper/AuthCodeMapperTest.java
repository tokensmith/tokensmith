package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PSQLException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
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
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    @Autowired
    private RandomString randomString;

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

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest, false, plainTextAuthCode);
        subject.insert(authCode);
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertDuplicateExpectDuplicateKeyException() throws URISyntaxException {

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

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest, false, plainTextAuthCode);
        subject.insert(authCode);

        // insert duplicate.
        authCode.setUuid(UUID.randomUUID());
        subject.insert(authCode);
    }

    @Test
    public void getByClientUUIDAndAuthCodeAndNotRevoked() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientUUIDAndAuthCodeAndNotRevoked(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());
        assertThat(actual.isRevoked()).isFalse();

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar).isNotNull();
        assertThat(ar.getUuid()).isEqualTo(expected.getAccessRequest().getUuid());

        // scopes
        assertThat(ar.getAccessRequestScopes()).isNotNull();
        assertThat(ar.getAccessRequestScopes().size()).isEqualTo(1);
        assertThat(ar.getAccessRequestScopes().get(0).getScope()).isNotNull();
        assertThat(ar.getAccessRequestScopes().get(0).getScope().getName()).isEqualTo("profile");
        assertThat(ar.getRedirectURI().isPresent()).isTrue();
        assertThat(ar.getRedirectURI().get().toString()).isEqualTo(FixtureFactory.SECURE_REDIRECT_URI);
    }

    @Test
    public void getByClientUUIDAndAuthCodeAndNotRevokedWhenRedirectURIIsNotPresent() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(false, false, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientUUIDAndAuthCodeAndNotRevoked(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual).isNotNull();
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar).isNotNull();
        assertThat(ar.getUuid()).isEqualTo(expected.getAccessRequest().getUuid());
        assertThat(ar.getAccessRequestScopes()).isNotNull();
        assertThat(ar.getAccessRequestScopes().size()).isEqualTo(1);
        assertThat(ar.getAccessRequestScopes().get(0).getScope()).isNotNull();
        assertThat(ar.getAccessRequestScopes().get(0).getScope().getName()).isEqualTo("profile");
        assertThat(ar.getRedirectURI().isPresent()).isFalse();
    }


    @Test
    public void getByClientUUIDAndAuthCodeAndNotRevokedWhenCodeIsRevoked() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(false, true, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientUUIDAndAuthCodeAndNotRevoked(expected.getAccessRequest().getClientUUID(), code);
        assertThat(actual).isNull();

    }
}
