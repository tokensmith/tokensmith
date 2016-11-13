package org.rootservices.authorization.oauth2.grant.refresh;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 10/8/16.
 */
public class IssueTokenRefreshGrantTest {
    private IssueTokenRefreshGrant subject;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private TokenChainRepository mockTokenChainRepository;
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
        subject = new IssueTokenRefreshGrant(
                mockRandomString,
                mockMakeBearerToken,
                mockTokenRepository,
                mockTokenChainRepository,
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
        UUID previousTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        String accessToken = "access-token";
        String headAccessToken = "head-access-token";
        String refreshAccessToken = "refresh- access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        Token headToken = FixtureFactory.makeOpenIdToken(headAccessToken);
        headToken.setCreatedAt(OffsetDateTime.now().minusDays(1));

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, headToken);

        ArgumentCaptor<TokenChain> tokenChainCaptor = ArgumentCaptor.forClass(TokenChain.class);
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);
        ArgumentCaptor<ClientToken> clientTokenArgumentCaptor = ArgumentCaptor.forClass(ClientToken.class);

        when(mockRandomString.run()).thenReturn(accessToken, refreshAccessToken);

        when(mockMakeBearerToken.run(accessToken, 3600L)).thenReturn(token);

        when(mockMakeRefreshToken.run(token, headToken, refreshAccessToken, 1209600L)).thenReturn(refreshToken);

        TokenResponse actual = subject.run(clientId, resourceOwner.getId(), previousTokenId, refreshTokenId, headToken, scopes);

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
        assertThat(actual.getTokenClaims().getAuthTime(), is(headToken.getCreatedAt().toEpochSecond()));

        verify(mockTokenRepository, times(1)).insert(token);

        verify(mockTokenChainRepository, times(1)).insert(tokenChainCaptor.capture());
        TokenChain tokenChain = tokenChainCaptor.getValue();
        assertThat(tokenChain, is(notNullValue()));
        assertThat(tokenChain.getId(), is(notNullValue()));
        assertThat(tokenChain.getToken().getId(), is(token.getId()));
        assertThat(tokenChain.getPreviousToken().getId(), is(previousTokenId));
        assertThat(tokenChain.getRefreshToken().getId(), is(refreshTokenId));

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

    @Test
    public void runWhenRefreshTokenUsedShouldThrowCompromisedRefreshTokenException() throws Exception {
        UUID clientId = UUID.randomUUID();
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        UUID previousTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        List<Scope> scopes = FixtureFactory.makeOpenIdScopes();

        String accessToken = "access-token";
        String headAccessToken = "head-access-token";
        String refreshAccessToken = "refresh- access-token";

        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        Token headToken = FixtureFactory.makeOpenIdToken(headAccessToken);

        when(mockRandomString.run()).thenReturn(accessToken, refreshAccessToken);

        when(mockMakeBearerToken.run(accessToken, 3600L)).thenReturn(token);

        DuplicateRecordException dre = new DuplicateRecordException("", null);
        doThrow(dre).when(mockTokenChainRepository).insert(any(TokenChain.class));

        CompromisedRefreshTokenException actual = null;
        try {
            subject.run(clientId, resourceOwner.getId(), previousTokenId, refreshTokenId, headToken, scopes);
        } catch (CompromisedRefreshTokenException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(dre));
        assertThat(actual.getMessage(), is("refresh token was already used"));
    }

}