package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private InsertTokenGraphCodeGrant mockInsertTokenGraph;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private AuthCodeTokenRepository mockAuthCodeTokenRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
    @Mock
    private ClientTokenRepository mockClientTokenRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenCodeGrant(
                mockInsertTokenGraph,
                mockTokenRepository,
                mockRefreshTokenRepository,
                mockAuthCodeTokenRepository,
                mockResourceOwnerTokenRepository,
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

        List<Scope> scopes = FixtureFactory.makeScopes();

        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId);
        when(mockInsertTokenGraph.insertTokenGraph(clientId, scopes)).thenReturn(tokenGraph);

        TokenResponse actual = subject.run(clientId, authCodeId, resourceOwnerId, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(tokenGraph.getPlainTextAccessToken()));
        assertThat(actual.getRefreshAccessToken(), is(tokenGraph.getPlainTextRefreshToken().get()));
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
        assertThat(actual.getTokenClaims().getAuthTime(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));

        // should insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(tokenGraph.getToken().getId()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should insert a resourceOwnerToken
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);
        verify(mockResourceOwnerTokenRepository).insert(resourceOwnerTokenCaptor.capture());

        ResourceOwnerToken actualROT = resourceOwnerTokenCaptor.getValue();
        assertThat(actualROT.getResourceOwner(), is(notNullValue()));
        assertThat(actualROT.getResourceOwner().getId(), is(resourceOwnerId));

        assertThat(actualROT.getId(), is(notNullValue()));
        assertThat(actualROT.getToken(), is(tokenGraph.getToken()));

        // should insert a client token record.
        ArgumentCaptor<ClientToken> clientTokenArgumentCaptor = ArgumentCaptor.forClass(ClientToken.class);
        verify(mockClientTokenRepository, times(1)).insert(clientTokenArgumentCaptor.capture());
        ClientToken actualCt = clientTokenArgumentCaptor.getValue();
        assertThat(actualCt.getId(), is(notNullValue()));
        assertThat(actualCt.getTokenId(), is(tokenGraph.getToken().getId()));
        assertThat(actualCt.getClientId(), is(clientId));

    }

    @Test
    public void runShouldThrowCompromisedCodeException() throws Exception{
        UUID clientId = UUID.randomUUID();
        UUID authCodeId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        List<Scope> scopes = FixtureFactory.makeScopes();

        TokenGraph tokenGraph = FixtureFactory.makeTokenGraph(clientId);
        when(mockInsertTokenGraph.insertTokenGraph(clientId, scopes)).thenReturn(tokenGraph);

        DuplicateRecordException duplicateRecordException = new DuplicateRecordException("", null);
        doThrow(duplicateRecordException).when(mockAuthCodeTokenRepository).insert(any(AuthCodeToken.class));

        CompromisedCodeException expected = null;

        try {
            subject.run(clientId, authCodeId, resourceOwnerId, scopes);
        } catch (CompromisedCodeException e) {
            expected = e;
            assertThat(expected.getError(), is("invalid_grant"));
            assertThat(expected.getCode(), is(ErrorCode.COMPROMISED_AUTH_CODE.getCode()));
            assertThat(expected.getMessage(), is(ErrorCode.COMPROMISED_AUTH_CODE.getDescription()));
        }

        assertThat(expected, is(notNullValue()));

        // should have attempted to insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(tokenGraph.getToken().getId()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should have rejected previous tokens.
        verify(mockTokenRepository).revokeByAuthCodeId(authCodeId);
        verify(mockAuthCodeRepository).revokeById(authCodeId);
        verify(mockRefreshTokenRepository).revokeByAuthCodeId(authCodeId);

        // should have rejected tokens just inserted.
        verify(mockTokenRepository).revokeById(tokenGraph.getToken().getId());
        verify(mockRefreshTokenRepository).revokeByTokenId(tokenGraph.getToken().getId());

        // should never insert anything else!
        verify(mockResourceOwnerTokenRepository, never()).insert(any(ResourceOwnerToken.class));
        verify(mockClientTokenRepository, never()).insert(any(ClientToken.class));
    }
}