package org.rootservices.authorization.oauth2.grant.password;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/18/16.
 */
public class IssueTokenPasswordGrantTest {
    private IssueTokenPasswordGrant subject;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private MakeRefreshToken mockMakeRefreshToken;
    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;
    @Mock
    private ClientTokenRepository mockClientTokenRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenPasswordGrant(
                mockRandomString,
                mockMakeBearerToken,
                mockTokenRepository,
                mockMakeRefreshToken,
                mockRefreshTokenRepository,
                mockResourceOwnerTokenRepository,
                mockTokenScopeRepository,
                mockClientTokenRepository,
                new TokenResponseBuilder(),
                "https://sso.rootservices.org"
        );
    }

    @Test
    public void runShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        String accessToken = "access-token";
        String headAccessToken = "head-access-token";
        String refreshAccessToken = "refresh-access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        token.setCreatedAt(OffsetDateTime.now());

        Token headToken = FixtureFactory.makeOpenIdToken(headAccessToken);
        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, headToken);
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);
        ArgumentCaptor<ClientToken> clientTokenArgumentCaptor = ArgumentCaptor.forClass(ClientToken.class);

        when(mockMakeBearerToken.run(accessToken)).thenReturn(token);
        when(mockMakeBearerToken.getSecondsToExpiration()).thenReturn(3600L);

        when(mockRandomString.run()).thenReturn(refreshAccessToken);
        when(mockMakeRefreshToken.run(token, token, refreshAccessToken)).thenReturn(refreshToken);

        TokenResponse actual = subject.run(clientId, resourceOwner.getId(), accessToken, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getRefreshAccessToken(), is(refreshAccessToken));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

        assertThat(actual.getTokenClaims(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getIssuer(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(actual.getTokenClaims().getAudience(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getAudience().size(), is(1));
        assertThat(actual.getTokenClaims().getAudience().get(0), is(clientId.toString()));
        assertThat(actual.getTokenClaims().getIssuedAt(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getExpirationTime(), is(notNullValue()));
        assertThat(actual.getTokenClaims().getAuthTime(), is(token.getCreatedAt().toEpochSecond()));

        verify(mockTokenRepository, times(1)).insert(token);

        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());
        TokenScope actualTokenScope = tokenScopeCaptor.getValue();
        assertThat(actualTokenScope.getId(), is(notNullValue()));
        assertThat(actualTokenScope.getTokenId(), is(token.getId()));
        assertThat(actualTokenScope.getScope(), is(scopes.get(0)));

        verify(mockResourceOwnerTokenRepository, times(1)).insert(resourceOwnerTokenCaptor.capture());
        ResourceOwnerToken actualRot = resourceOwnerTokenCaptor.getValue();
        assertThat(actualRot.getId(), is(notNullValue()));
        assertThat(actualRot.getToken(), is(token));
        assertThat(actualRot.getResourceOwner().getId(), is(resourceOwner.getId()));

        verify(mockClientTokenRepository, times(1)).insert(clientTokenArgumentCaptor.capture());
        ClientToken actualCt = clientTokenArgumentCaptor.getValue();
        assertThat(actualCt.getId(), is(notNullValue()));
        assertThat(actualCt.getTokenId(), is(token.getId()));
        assertThat(actualCt.getClientId(), is(clientId));

    }
}