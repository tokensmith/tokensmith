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

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 4/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class AuthCodeTokenMapperTest {

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
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AuthCodeTokenMapper subject;

    public AuthCode insertAuthCodeForTest() throws Exception {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        clientRepository.insert(client);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        resourceOwnerRepository.insert(resourceOwner);

        AccessRequest accessRequest = FixtureFactory.makeAccessRequest(
                resourceOwner.getUuid(),
                client.getUuid()
        );
        accessRequestRepository.insert(accessRequest);

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = FixtureFactory.makeAuthCode(accessRequest, false, plainTextAuthCode);
        authCodeRepository.insert(authCode);

        return authCode;
    }

    public Token insertTokenForTest() throws DuplicateRecordException {
        Token token = FixtureFactory.makeToken();
        tokenRepository.insert(token);

        return token;
    }

    @Test
    public void testInsert() throws Exception {

        AuthCode authCode = insertAuthCodeForTest();
        Token token = insertTokenForTest();

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(token.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        subject.insert(authCodeToken);
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertDuplicateExpectDuplicateKeyException() throws Exception {

        AuthCode authCode = insertAuthCodeForTest();
        Token token = insertTokenForTest();

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(token.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        subject.insert(authCodeToken);
        subject.insert(authCodeToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception{
        AuthCode authCode = insertAuthCodeForTest();
        Token token = insertTokenForTest();

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(token.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        subject.insert(authCodeToken);

        AuthCodeToken actual = subject.getByTokenId(token.getUuid());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAuthCodeId(), is(authCode.getUuid()));
        assertThat(actual.getTokenId(), is(token.getUuid()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getUpdatedAt(), is(notNullValue()));
    }
}