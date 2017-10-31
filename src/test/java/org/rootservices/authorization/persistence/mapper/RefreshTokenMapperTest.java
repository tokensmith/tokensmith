package org.rootservices.authorization.persistence.mapper;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfClientTokenReady;
import org.hamcrest.CoreMatchers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:spring-auth-test.xml"})
@Transactional
public class RefreshTokenMapperTest {
    @Autowired
    private RefreshTokenMapper subject;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private ResourceOwnerTokenMapper resourceOwnerTokenMapper;
    @Autowired
    private TokenAudienceMapper tokenAudienceMapper;
    @Autowired
    private TokenLeadTokenMapper tokenLeadTokenMapper;
    @Autowired
    private ScopeMapper scopeMapper;
    @Autowired
    private TokenScopeMapper tokenScopeMapper;
    @Autowired
    private LoadConfClientTokenReady loadConfClientTokenReady;
    @Autowired
    private AuthCodeTokenMapper authCodeTokenMapper;
    @Autowired
    private RandomString randomString;

    public Token loadToken(String accessToken, OffsetDateTime expiresAt, boolean revoked, UUID clientId) {
        Token token = FixtureFactory.makeOAuthToken(accessToken, clientId, new ArrayList<>());
        token.setExpiresAt(expiresAt);
        token.setRevoked(revoked);

        tokenMapper.insert(token);
        return token;
    }

    public Token loadTokenWithScopes(String accessToken, OffsetDateTime expiresAt, boolean revoked, UUID clientId) {
        Token token = loadToken(accessToken, expiresAt, revoked, clientId);
        Scope scope = FixtureFactory.makeScope();
        scope.setName("address");
        scopeMapper.insert(scope);

        TokenScope tokenScope = new TokenScope();
        tokenScope.setId(UUID.randomUUID());
        tokenScope.setTokenId(token.getId());
        tokenScope.setScope(scope);
        tokenScopeMapper.insert(tokenScope);

        return token;
    }

    public RefreshToken prepare() throws Exception {
        return prepare(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusSeconds(1209600));
    }

    public RefreshToken prepare(OffsetDateTime tokenExpiresAt, OffsetDateTime refreshTokenExpiresAt) throws Exception {
        // begin prepare db for test.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfClientTokenReady.run(true, false, plainTextAuthCode);

        String accessToken = randomString.run();
        String leadAccessToken = randomString.run();

        UUID clientId = authCode.getAccessRequest().getClientId();
        Token leadToken = loadToken(leadAccessToken, OffsetDateTime.now().minusDays(10), false, clientId);

        ResourceOwner ro = new ResourceOwner();
        ro.setId(authCode.getAccessRequest().getResourceOwnerId());

        Token token = loadTokenWithScopes(accessToken, tokenExpiresAt, false, clientId);
        token.setLeadToken(leadToken);

        // lead token rot.
        ResourceOwnerToken leadRot = new ResourceOwnerToken();
        leadRot.setId(UUID.randomUUID());
        leadRot.setResourceOwner(ro);
        leadRot.setToken(leadToken);
        resourceOwnerTokenMapper.insert(leadRot);

        // token rot
        ResourceOwnerToken rot = new ResourceOwnerToken();
        rot.setId(UUID.randomUUID());
        rot.setResourceOwner(ro);
        rot.setToken(token);
        resourceOwnerTokenMapper.insert(rot);

        TokenLeadToken tlt = new TokenLeadToken();
        tlt.setId(UUID.randomUUID());
        tlt.setTokenId(token.getId());
        tlt.setLeadTokenId(leadToken.getId());
        tokenLeadTokenMapper.insert(tlt);

        TokenAudience tokenAudience = new TokenAudience();
        tokenAudience.setId(UUID.randomUUID());
        tokenAudience.setTokenId(token.getId());
        tokenAudience.setClientId(clientId);
        tokenAudienceMapper.insert(tokenAudience);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getId());
        authCodeToken.setTokenId(token.getId());
        authCodeTokenMapper.insert(authCodeToken);

        // XXX: should it insert a client token?

        String refreshAccessToken = randomString.run();
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);
        refreshToken.setExpiresAt(refreshTokenExpiresAt);
        subject.insert(refreshToken);

        return refreshToken;
    }

    @Test
    public void insertShouldBeOk() throws Exception {
        String accessToken = "access-token";
        String refreshAccessToken = "refresh-access-token";

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        Token token = loadToken(accessToken, OffsetDateTime.now(), false, client.getId());
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);

        subject.insert(refreshToken);

        RefreshToken actual = subject.getByTokenId(token.getId());
        assertThat(actual, is(notNullValue()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void insertShouldThrowDuplicateKeyException() throws Exception {
        String accessToken = "access-token";
        String refreshAccessToken = "refresh-access-token";

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        Token token = loadToken(accessToken, OffsetDateTime.now(), false, client.getId());
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);

        subject.insert(refreshToken);

        refreshToken.setId(UUID.randomUUID());
        subject.insert(refreshToken);
    }

    @Test
    public void getByTokenIdShouldBeOk() throws Exception {
        String accessToken = "access-token";
        String refreshAccessToken = "refresh-access-token";

        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        clientMapper.insert(client);

        Token token = loadToken(accessToken, OffsetDateTime.now(), false, client.getId());
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token);

        subject.insert(refreshToken);

        RefreshToken actual = subject.getByTokenId(token.getId());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(refreshToken.getExpiresAt().toEpochSecond()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));
    }

    @Test
    public void getByClientIdAndTokenShouldBeOk() throws Exception {
        RefreshToken refreshToken = prepare();
        UUID clientId = refreshToken.getToken().getClientId();

        String hashedRefreshAccessToken = new String(refreshToken.getAccessToken());
        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, hashedRefreshAccessToken);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.getTokenId(), is(refreshToken.getTokenId()));
        assertThat(actual.getAccessToken(), is(refreshToken.getAccessToken()));
        assertThat(actual.getExpiresAt().toEpochSecond(), is(refreshToken.getExpiresAt().toEpochSecond()));
        assertThat(actual.getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken().getId(), is(refreshToken.getToken().getId()));
        assertThat(actual.getToken().getToken(), is(notNullValue()));
        assertThat(actual.getToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));

        assertThat(actual.getToken().getAudience(), is(notNullValue()));
        assertThat(actual.getToken().getAudience().size(), is(1));
        assertThat(actual.getToken().getAudience().get(0).getId(), is(clientId));
        assertThat(actual.getToken().getAudience().get(0).getRedirectURI(), is(notNullValue()));
        assertThat(actual.getToken().getAudience().get(0).getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken().getExpiresAt(), is(notNullValue()));
        assertThat(actual.getToken().getCreatedAt(), is(notNullValue()));

        assertThat(actual.getToken().getLeadToken(), is(notNullValue()));
        assertThat(actual.getToken().getLeadToken().getId(), is(refreshToken.getToken().getLeadToken().getId()));
        assertThat(actual.getToken().getLeadToken().isRevoked(), is(false));
        assertThat(actual.getToken().getLeadToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));
        assertThat(actual.getToken().getLeadToken().getClientId(), is(refreshToken.getToken().getClientId()));
        assertThat(actual.getToken().getLeadToken().getCreatedAt(), is(notNullValue()));
        assertThat(actual.getToken().getLeadToken().getExpiresAt(), is(notNullValue()));

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
        RefreshToken refreshToken = prepare();
        UUID clientId = refreshToken.getToken().getClientId();
        String hashedRefreshAccessToken = new String(refreshToken.getAccessToken());

        subject.revokeByTokenId(refreshToken.getToken().getId());

        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, hashedRefreshAccessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenRefreshTokenIsExpiredShouldBeNull() throws Exception {
        RefreshToken refreshToken = prepare(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().minusHours(1));
        UUID clientId = refreshToken.getToken().getClientId();
        String hashedRefreshAccessToken = new String(refreshToken.getAccessToken());

        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, hashedRefreshAccessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenTokenIsNotExpiredShouldBeNull() throws Exception {
        RefreshToken refreshToken = prepare(OffsetDateTime.now().plusSeconds(1209600), OffsetDateTime.now().minusHours(1));
        UUID clientId = refreshToken.getToken().getClientId();
        String hashedRefreshAccessToken = new String(refreshToken.getAccessToken());

        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, hashedRefreshAccessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getByClientIdAndAccessTokenWhenTokenRevokedShouldBeNull() throws Exception {
        RefreshToken refreshToken = prepare();
        UUID clientId = refreshToken.getToken().getClientId();
        String hashedRefreshAccessToken = new String(refreshToken.getAccessToken());

        tokenMapper.revokeById(refreshToken.getToken().getId());

        RefreshToken actual = subject.getByClientIdAndAccessToken(clientId, hashedRefreshAccessToken);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void revokeByAuthCodeIdShouldBeOk() throws Exception {
        RefreshToken refreshToken = prepare();
        AuthCodeToken act =  authCodeTokenMapper.getByTokenId(refreshToken.getToken().getId());

        assertThat(refreshToken.isRevoked(), is(false));

        subject.revokeByAuthCodeId(act.getAuthCodeId());

        RefreshToken actual = subject.getByTokenId(refreshToken.getToken().getId());

        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.isRevoked(), is(true));
    }

    @Test
    public void getByResourceOwnerShouldBeOk() throws Exception {
        RefreshToken refreshToken = prepare();

        String hashedAccessToken = new String(refreshToken.getToken().getToken());
        ResourceOwnerToken rot = resourceOwnerTokenMapper.getByAccessToken(hashedAccessToken);

        List<RefreshToken> actual = subject.getByResourceOwner(rot.getResourceOwner().getId());
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
    }

    @Test
    public void revokeByTokenIdShouldBeOk() throws Exception {
        RefreshToken refreshToken = prepare();
        UUID tokenId = refreshToken.getToken().getId();

        assertThat(refreshToken.isRevoked(), is(false));

        subject.revokeByTokenId(tokenId);

        RefreshToken actual = subject.getByTokenId(tokenId);

        assertThat(actual.getId(), is(refreshToken.getId()));
        assertThat(actual.isRevoked(), is(true));
    }

    @Test
    public void revokeActiveShouldBeOK() throws Exception {
        RefreshToken refreshToken = prepare();

        assertThat(refreshToken.isRevoked(), is(false));

        String hashedAccessToken = new String(refreshToken.getToken().getToken());
        ResourceOwnerToken rot = resourceOwnerTokenMapper.getByAccessToken(hashedAccessToken);

        assertThat(rot, is(notNullValue()));

        // revoke active.
        subject.revokeActive(rot.getResourceOwner().getId());

        List<RefreshToken> refreshTokens = subject.getByResourceOwner(rot.getResourceOwner().getId());
        assertThat(refreshTokens, is(notNullValue()));
        assertThat(refreshTokens.size(), is(1));
        assertThat(refreshTokens.get(0).isRevoked(), is(true));
    }

}