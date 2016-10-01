package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
    private TokenRepository tokenRepository;
    @Autowired
    private AuthCodeTokenRepository authCodeTokenRepository;

    @Autowired
    private AuthCodeMapper subject;

    @Test
    public void insert() throws Exception {

        // prepare db for test.
        Client client = FixtureFactory.makeCodeClientWithScopes();
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
    public void insertDuplicateExpectDuplicateKeyException() throws Exception {

        // prepare db for test.
        Client client = FixtureFactory.makeCodeClientWithScopes();
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
    public void getByClientIdAndAuthCodeShouldBeOk() throws Exception {

        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientIdAndAuthCode(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual, notNullValue());
        assertThat(actual.getUuid(), is(expected.getUuid()));
        assertThat(actual.isRevoked(), is(false));

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar, notNullValue());
        assertThat(ar.getUuid(), is(expected.getAccessRequest().getUuid()));
        assertThat(ar.getResourceOwnerUUID(), is(expected.getAccessRequest().getResourceOwnerUUID()));

        // scopes
        assertThat(ar.getAccessRequestScopes(), is(notNullValue()));
        assertThat(ar.getAccessRequestScopes().size(), is(1));
        assertThat(ar.getAccessRequestScopes().get(0).getScope(), is(notNullValue()));
        assertThat(ar.getAccessRequestScopes().get(0).getScope().getName(), is("profile"));
        assertThat(ar.getRedirectURI().isPresent(), is(true));
        assertThat(ar.getRedirectURI().get().toString(), is(FixtureFactory.SECURE_REDIRECT_URI));

        assertThat(actual.getToken(), is(nullValue()));
    }

    @Test
    public void getByClientIdAndAuthCodeWhenRedirectURIIsNotPresent() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(false, false, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientIdAndAuthCode(expected.getAccessRequest().getClientUUID(), code);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUuid(), is(expected.getUuid()));

        // access request.
        AccessRequest ar = actual.getAccessRequest();
        assertThat(ar, is(notNullValue()));
        assertThat(ar.getUuid(), is(expected.getAccessRequest().getUuid()));
        assertThat(ar.getAccessRequestScopes(), is(notNullValue()));
        assertThat(ar.getAccessRequestScopes().size(), is(1));
        assertThat(ar.getAccessRequestScopes().get(0).getScope(), is(notNullValue()));
        assertThat(ar.getAccessRequestScopes().get(0).getScope().getName(), is("profile"));
        assertThat(ar.getRedirectURI().isPresent(), is(false));

        assertThat(actual.getToken(), is(nullValue()));
    }


    @Test
    public void getByClientIdAndAuthCodeWhenCodeIsRevoked() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode expected = loadConfidentialClientTokenReady.run(false, true, plainTextAuthCode);

        String code = new String(expected.getCode());
        AuthCode actual = subject.getByClientIdAndAuthCode(expected.getAccessRequest().getClientUUID(), code);
        assertThat(actual, is(nullValue()));

    }

    @Test
    public void getByClientIdAndAuthCodeWhenTokenIsPresent() throws Exception {
        // begin - prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(false, false, plainTextAuthCode);

        Token token = FixtureFactory.makeOpenIdToken();
        tokenRepository.insert(token);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(token.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        authCodeTokenRepository.insert(authCodeToken);
        // end - prepare db for test.

        String code = new String(authCode.getCode());

        AuthCode actual = subject.getByClientIdAndAuthCode(authCode.getAccessRequest().getClientUUID(), code);
        assertThat(actual, is(notNullValue()));

        // this test is just to make sure a token is present
        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken().getUuid(), is(token.getUuid()));
        assertThat(actual.getToken().isRevoked(), is(false));
        assertThat(actual.getToken().getExpiresAt(), is(token.getExpiresAt()));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void revokeByIdShouldRevoke() throws Exception{
        String plainTextAuthCode = randomString.run();
        AuthCode authCodeToRevoke = loadConfidentialClientTokenReady.run(false, false, plainTextAuthCode);

        // insert one more auth code to make sure it only updates one and not all of em.
        String plainTextAuthCode2 = randomString.run();
        AuthCode authCodeNotRevoked = loadConfidentialClientTokenReady.run(false, false, plainTextAuthCode2);

        subject.revokeById(authCodeToRevoke.getUuid());

        AuthCode actual = subject.getById(authCodeToRevoke.getUuid());

        // the one it should have revoked.
        assertThat(actual.getUuid(), is(authCodeToRevoke.getUuid()));
        assertThat(actual.isRevoked(), is(true));

        // the one it should not have revoked.
        AuthCode actualNotRevoked = subject.getById(authCodeNotRevoked.getUuid());
        assertThat(actualNotRevoked.getUuid(), is(authCodeNotRevoked.getUuid()));
        assertThat(actualNotRevoked.isRevoked(), is(false));
    }
}
