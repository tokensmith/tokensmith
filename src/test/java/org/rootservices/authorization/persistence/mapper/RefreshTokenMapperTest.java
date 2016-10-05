package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.AuthCodeToken;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.repository.AuthCodeTokenRepository;
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
 * Created by tommackenzie on 10/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class RefreshTokenMapperTest {
    @Autowired
    private RefreshTokenMapper subject;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;
    @Autowired
    private AuthCodeTokenMapper authCodeTokenMapper;
    @Autowired
    private RandomString randomString;

    public UUID loadToken() {
        Token token = FixtureFactory.makeOAuthToken();
        tokenMapper.insert(token);
        return token.getId();
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        String accessToken = new String(refreshToken.getToken());
        RefreshToken actual = subject.getByToken(accessToken);
        assertThat(actual, is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        refreshToken.setId(UUID.randomUUID());
        subject.insert(refreshToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        RefreshToken actual = subject.getByTokenId(tokenId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getToken(), is(refreshToken.getToken()));
        assertThat(actual.getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByTokenShouldBeOk() throws Exception {
        UUID tokenId = loadToken();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        String accessToken = new String(refreshToken.getToken());
        RefreshToken actual = subject.getByToken(accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getToken(), is(refreshToken.getToken()));
        assertThat(actual.getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Token token = FixtureFactory.makeOpenIdToken();
        tokenMapper.insert(token);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(token.getId());
        authCodeTokenMapper.insert(authCodeToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(token.getId());
        subject.insert(refreshToken);
        // end prepare db for test.

        assertThat(refreshToken.isRevoked(), is(false));

        subject.revokeByAuthCodeId(authCode.getId());

        String accessToken = new String(refreshToken.getToken());
        RefreshToken actual = subject.getByToken(accessToken);

        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.isRevoked(), is(true));
    }

    @Test
    public void revokeByTokenIdShouldBeOk() throws Exception {
        Token token = FixtureFactory.makeOpenIdToken();
        tokenMapper.insert(token);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(token.getId());
        subject.insert(refreshToken);

        assertThat(refreshToken.isRevoked(), is(false));

        subject.revokeByTokenId(token.getId());

        String accessToken = new String(refreshToken.getToken());
        RefreshToken actual = subject.getByToken(accessToken);

        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.isRevoked(), is(true));
    }

}