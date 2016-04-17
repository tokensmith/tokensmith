package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import helper.fixture.persistence.LoadClientWithScopes;
import org.junit.Before;
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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by tommackenzie on 5/23/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class TokenMapperTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private RandomString randomString;
    @Autowired
    private AuthCodeTokenRepository authCodeTokenRepository;
    @Autowired
    private TokenMapper subject;

    @Test
    public void insert() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token);
    }

    @Test (expected = DuplicateKeyException.class)
    public void insertExpectDuplicateAuthorizationCode() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        // first token.
        Token token1 = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token1);

        // second token.
        Token token2 = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token2);
    }

    @Test
    public void revokeShouldRevoke() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Token tokenToRevoke = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(tokenToRevoke);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(tokenToRevoke.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        authCodeTokenRepository.insert(authCodeToken);

        subject.revoke(authCode.getUuid());

        Token revokedToken = subject.getByAuthCodeId(authCode.getUuid());
        assertTrue(revokedToken.isRevoked());

        /**
         * TODO: make sure it only revokes a token connected to the auth code id.
         */
    }
}
