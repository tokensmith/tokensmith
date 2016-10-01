package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        Token token = FixtureFactory.makeOpenIdToken();
        subject.insert(token);
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Token tokenToRevoke = FixtureFactory.makeOpenIdToken();
        subject.insert(tokenToRevoke);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setTokenId(tokenToRevoke.getUuid());
        authCodeToken.setAuthCodeId(authCode.getUuid());

        authCodeTokenRepository.insert(authCodeToken);
        // end prepare db for test.

        subject.revokeByAuthCodeId(authCode.getUuid());

        Token revokedToken = subject.getByAuthCodeId(authCode.getUuid());
        assertTrue(revokedToken.isRevoked());

        /**
         * TODO: make sure it only revokes a token connected to the auth code id.
         */
    }

    @Test
    public void getByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Token token = FixtureFactory.makeOpenIdToken();
        subject.insert(token);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getUuid());
        authCodeToken.setTokenId(token.getUuid());
        authCodeTokenRepository.insert(authCodeToken);
        // end prepare db for test.

        Token actual = subject.getByAuthCodeId(authCode.getUuid());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUuid(), is(token.getUuid()));
        assertThat(actual.getToken(), is(token.getToken()));
        assertThat(actual.isRevoked(), is(false));
        assertThat(actual.getGrantType(), is(token.getGrantType()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
        assertThat(actual.getExpiresAt(), is(token.getExpiresAt()));
    }
}
