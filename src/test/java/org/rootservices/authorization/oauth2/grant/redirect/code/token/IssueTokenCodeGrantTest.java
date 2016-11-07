package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Created by tommackenzie on 8/28/16.
 */
public class IssueTokenCodeGrantTest {
    private IssueTokenCodeGrant subject;
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
    private AuthCodeTokenRepository mockAuthCodeTokenRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
    @Mock
    private ClientTokenRepository mockClientTokenRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenCodeGrant(
                mockRandomString,
                mockMakeBearerToken,
                mockTokenRepository,
                mockMakeRefreshToken,
                mockRefreshTokenRepository,
                mockAuthCodeTokenRepository,
                mockResourceOwnerTokenRepository,
                mockTokenScopeRepository,
                mockAuthCodeRepository,
                mockClientTokenRepository,
                new TokenResponseBuilder(),
                "https://sso.rootservices.org"
        );
    }

    @Test
    public void runShouldReturnTokenResponse() throws Exception {
        UUID clientId = UUID.randomUUID();
        UUID authCodeId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        String plainTextToken = "plain-text-token";
        List<AccessRequestScope> accessRequestScopes = FixtureFactory.makeAccessRequestScopes();

        Token token = FixtureFactory.makeOpenIdToken(plainTextToken);
        token.setCreatedAt(OffsetDateTime.now());

        when(mockMakeBearerToken.run(plainTextToken)).thenReturn(token);
        when(mockMakeBearerToken.getSecondsToExpiration()).thenReturn(3600L);

        String refreshAccessToken = "refresh-token";

        when(mockRandomString.run()).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, token);

        when(mockMakeRefreshToken.run(token, token, refreshAccessToken)).thenReturn(refreshToken);

        TokenResponse actual = subject.run(clientId, authCodeId, resourceOwnerId, accessRequestScopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(plainTextToken));
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

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should insert a refresh token.
        verify(mockRefreshTokenRepository, times(1)).insert(refreshToken);

        // should insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(token.getId()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should insert a resourceOwnerToken
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);
        verify(mockResourceOwnerTokenRepository).insert(resourceOwnerTokenCaptor.capture());

        ResourceOwnerToken actualROT = resourceOwnerTokenCaptor.getValue();
        assertThat(actualROT.getResourceOwner(), is(notNullValue()));
        assertThat(actualROT.getResourceOwner().getId(), is(resourceOwnerId));

        assertThat(actualROT.getId(), is(notNullValue()));
        assertThat(actualROT.getToken(), is(token));

        // should insert a client token record.
        ArgumentCaptor<ClientToken> clientTokenArgumentCaptor = ArgumentCaptor.forClass(ClientToken.class);
        verify(mockClientTokenRepository, times(1)).insert(clientTokenArgumentCaptor.capture());
        ClientToken actualCt = clientTokenArgumentCaptor.getValue();
        assertThat(actualCt.getId(), is(notNullValue()));
        assertThat(actualCt.getTokenId(), is(token.getId()));
        assertThat(actualCt.getClientId(), is(clientId));

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(2)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(0).getScope(), is(accessRequestScopes.get(0).getScope()));

        assertThat(actualTokenScopes.get(1).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(1).getTokenId(), is(token.getId()));
        assertThat(actualTokenScopes.get(1).getScope(), is(accessRequestScopes.get(1).getScope()));
    }

    @Test
    public void runShouldThrowCompromisedCodeException() throws Exception{
        UUID clientId = UUID.randomUUID();
        UUID authCodeId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        String plainTextToken = "plain-text-token";
        List<AccessRequestScope> accessRequestScopes = FixtureFactory.makeAccessRequestScopes();

        Token token = FixtureFactory.makeOpenIdToken(plainTextToken);
        when(mockMakeBearerToken.run("plain-text-token")).thenReturn(token);

        String refreshAccessToken = "refresh-access-token";
        when(mockRandomString.run()).thenReturn(plainTextToken, refreshAccessToken);

        RefreshToken refreshToken = FixtureFactory.makeRefreshToken(refreshAccessToken, token, token);
        when(mockMakeRefreshToken.run(token, token, refreshAccessToken)).thenReturn(refreshToken);


        DuplicateRecordException duplicateRecordException = new DuplicateRecordException("", null);
        doThrow(duplicateRecordException).when(mockAuthCodeTokenRepository).insert(any(AuthCodeToken.class));

        CompromisedCodeException expected = null;

        try {
            subject.run(clientId, authCodeId, resourceOwnerId, accessRequestScopes);
        } catch (CompromisedCodeException e) {
            expected = e;
            assertThat(expected.getError(), is("invalid_grant"));
            assertThat(expected.getCode(), is(ErrorCode.COMPROMISED_AUTH_CODE.getCode()));
            assertThat(expected.getMessage(), is(ErrorCode.COMPROMISED_AUTH_CODE.getDescription()));
        }

        assertThat(expected, is(notNullValue()));

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should insert a refresh token
        verify(mockRefreshTokenRepository).insert(refreshToken);

        // should have attempted to insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(token.getId()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should have rejected previous tokens.
        verify(mockTokenRepository).revokeByAuthCodeId(authCodeId);
        verify(mockAuthCodeRepository).revokeById(authCodeId);
        verify(mockRefreshTokenRepository).revokeByAuthCodeId(authCodeId);

        // should have rejected tokens just inserted.
        verify(mockTokenRepository).revokeById(token.getId());
        verify(mockRefreshTokenRepository).revokeByTokenId(token.getId());

        // should never insert anything else!
        verify(mockResourceOwnerTokenRepository, never()).insert(any(ResourceOwnerToken.class));
        verify(mockClientTokenRepository, never()).insert(any(ClientToken.class));
        verify(mockTokenScopeRepository, never()).insert(any(TokenScope.class));
    }
}