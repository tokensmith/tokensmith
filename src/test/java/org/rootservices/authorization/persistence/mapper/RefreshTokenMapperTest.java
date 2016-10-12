package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
    private ScopeMapper scopeMapper;
    @Autowired
    private TokenScopeMapper tokenScopeMapper;
    @Autowired
    private ClientTokenMapper clientTokenMapper;
    @Autowired
    private LoadConfClientTokenReady loadConfClientTokenReady;
    @Autowired
    private AuthCodeTokenMapper authCodeTokenMapper;
    @Autowired
    private RandomString randomString;

    public UUID loadToken(OffsetDateTime expiresAt, boolean revoked) {
        Token token = FixtureFactory.makeOAuthToken();
        token.setExpiresAt(expiresAt);
        token.setRevoked(revoked);

        tokenMapper.insert(token);
        return token.getId();
    }

    public UUID loadTokenWithScopes(OffsetDateTime expiresAt, boolean revoked) {
        UUID tokenId = loadToken(expiresAt, revoked);
        Scope scope = FixtureFactory.makeScope();
        scope.setName("address");
        scopeMapper.insert(scope);

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(tokenId);
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);

        return tokenId;
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        UUID tokenId = loadToken(OffsetDateTime.now(), false);
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        RefreshToken actual = subject.getByTokenId(tokenId);
        assertThat(actual, is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() throws Exception {
        UUID tokenId = loadToken(OffsetDateTime.now(), false);
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        refreshToken.setId(UUID.randomUUID());
        subject.insert(refreshToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception {
        UUID tokenId = loadToken(OffsetDateTime.now(), false);
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);

        subject.insert(refreshToken);

        RefreshToken actual = subject.getByTokenId(tokenId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByClientIdAndTokenShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        UUID tokenId = loadTokenWithScopes(OffsetDateTime.now().minusHours(1), false);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(tokenId);
        authCodeTokenMapper.insert(authCodeToken);

        UUID clientId = authCode.getAccessRequest().getClientId();
        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(tokenId);
        clientTokenMapper.insert(clientToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        subject.insert(refreshToken);
        // end prepare db for test.

        String accessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, accessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getExpiresAt(), is(refreshToken.getExpiresAt()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken().getId(), is(notNullValue()));
        assertThat(actual.getToken().getToken(), is(notNullValue()));
        assertThat(actual.getToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));
        assertThat(actual.getToken().getExpiresAt(), is(notNullValue()));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken().getTokenScopes(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().size(), is(1));
        assertThat(actual.getToken().getTokenScopes().get(0).getId(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().get(0).getTokenId(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().get(0).getScope(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().get(0).getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken().getTokenScopes().get(0).getScope().getId(), is(notNullValue()));
        assertThat(actual.getToken().getTokenScopes().get(0).getScope().getName(), is("address"));
        assertThat(actual.getToken().getTokenScopes().get(0).getScope().getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenRefreshTokenIsRevokedShouldBeNull() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        UUID tokenId = loadTokenWithScopes(OffsetDateTime.now().minusHours(1), false);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(tokenId);
        authCodeTokenMapper.insert(authCodeToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        subject.insert(refreshToken);

        subject.revokeByTokenId(tokenId);
        // end prepare db for test.

        UUID clientId = authCode.getAccessRequest().getClientId();
        String accessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, accessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByTokenWhenRefreshTokenIsExpiredShouldBeNull() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        UUID tokenId = loadTokenWithScopes(OffsetDateTime.now().minusHours(1), false);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(tokenId);
        authCodeTokenMapper.insert(authCodeToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        refreshToken.setExpiresAt(OffsetDateTime.now().minusHours(1));
        subject.insert(refreshToken);
        // end prepare db for test.

        UUID clientId = authCode.getAccessRequest().getClientId();
        String accessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, accessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenTokenIsNotExpiredShouldBeNull() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        UUID tokenId = loadTokenWithScopes(OffsetDateTime.now().plusHours(1), false);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(tokenId);
        authCodeTokenMapper.insert(authCodeToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        subject.insert(refreshToken);
        // end prepare db for test.

        UUID clientId = authCode.getAccessRequest().getClientId();
        String accessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, accessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenTokenRevokedShouldBeNull() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        UUID tokenId = loadTokenWithScopes(OffsetDateTime.now().minusHours(1), true);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(tokenId);
        authCodeTokenMapper.insert(authCodeToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(tokenId);
        subject.insert(refreshToken);
        // end prepare db for test.

        UUID clientId = authCode.getAccessRequest().getClientId();
        String accessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, accessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

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

        RefreshToken actual = subject.getByTokenId(token.getId());

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

        RefreshToken actual = subject.getByTokenId(token.getId());

        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.isRevoked(), is(true));
    }

}