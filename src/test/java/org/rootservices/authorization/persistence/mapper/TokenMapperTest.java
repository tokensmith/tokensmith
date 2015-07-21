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
    private TokenMapper subject;

    @Test
    public void insert() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token);
    }

    @Test (expected = DuplicateKeyException.class)
    public void insertExpectDuplicateAuthorizationCode() throws URISyntaxException, DuplicateRecordException {
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
    public void revokeExpectRevokeIsTrue() throws DuplicateRecordException, URISyntaxException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Token token = FixtureFactory.makeToken(authCode.getUuid());
        subject.insert(token);

        subject.revoke(authCode.getUuid());

        Token revokedToken = subject.getByAuthCodeUUID(authCode.getUuid());
        assertTrue(revokedToken.isRevoked());
    }
}
