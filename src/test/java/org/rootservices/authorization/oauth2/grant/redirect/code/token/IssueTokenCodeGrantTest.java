package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 8/28/16.
 */
public class IssueTokenCodeGrantTest {
    private IssueTokenCodeGrant subject;
    @Mock
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private AuthCodeTokenRepository mockAuthCodeTokenRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenCodeGrant(
                mockMakeBearerToken,
                mockTokenRepository,
                mockAuthCodeTokenRepository,
                mockResourceOwnerTokenRepository,
                mockTokenScopeRepository,
                mockAuthCodeRepository
        );
    }

    @Test
    public void runShouldReturnToken() throws Exception {
        UUID authCodeId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        String plainTextToken = "plain-text-token";
        List<AccessRequestScope> accessRequestScopes = FixtureFactory.makeAccessRequestScopes();

        Token token = FixtureFactory.makeOpenIdToken();
        when(mockMakeBearerToken.run("plain-text-token")).thenReturn(token);

        subject.run(authCodeId, resourceOwnerId, plainTextToken, accessRequestScopes);

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(token.getUuid()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should insert a resourceOwnerToken
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);
        verify(mockResourceOwnerTokenRepository).insert(resourceOwnerTokenCaptor.capture());

        ResourceOwnerToken actualROT = resourceOwnerTokenCaptor.getValue();
        assertThat(actualROT.getResourceOwner(), is(notNullValue()));
        assertThat(actualROT.getResourceOwner().getUuid(), is(resourceOwnerId));

        assertThat(actualROT.getId(), is(notNullValue()));
        assertThat(actualROT.getToken(), is(token));

        // should insert token scopes.
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        verify(mockTokenScopeRepository, times(2)).insert(tokenScopeCaptor.capture());

        List<TokenScope> actualTokenScopes = tokenScopeCaptor.getAllValues();

        assertThat(actualTokenScopes.get(0).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(0).getTokenId(), is(token.getUuid()));
        assertThat(actualTokenScopes.get(0).getScope(), is(accessRequestScopes.get(0).getScope()));

        assertThat(actualTokenScopes.get(1).getId(), is(notNullValue()));
        assertThat(actualTokenScopes.get(1).getTokenId(), is(token.getUuid()));
        assertThat(actualTokenScopes.get(1).getScope(), is(accessRequestScopes.get(1).getScope()));
    }

    @Test
    public void runShouldThrowCompromisedCodeException() throws Exception{
        UUID authCodeId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        String plainTextToken = "plain-text-token";
        List<AccessRequestScope> accessRequestScopes = FixtureFactory.makeAccessRequestScopes();

        Token token = FixtureFactory.makeOpenIdToken();
        when(mockMakeBearerToken.run("plain-text-token")).thenReturn(token);

        DuplicateRecordException duplicateRecordException = new DuplicateRecordException("", null);
        doThrow(duplicateRecordException).when(mockAuthCodeTokenRepository).insert(any(AuthCodeToken.class));

        CompromisedCodeException expected = null;

        try {
            subject.run(authCodeId, resourceOwnerId, plainTextToken, accessRequestScopes);
        } catch (CompromisedCodeException e) {
            expected = e;
            assertThat(expected.getError(), is("invalid_grant"));
            assertThat(expected.getCode(), is(ErrorCode.COMPROMISED_AUTH_CODE.getCode()));
            assertThat(expected.getMessage(), is(ErrorCode.COMPROMISED_AUTH_CODE.getDescription()));
        }

        assertThat(expected, is(notNullValue()));

        // should insert a token
        verify(mockTokenRepository).insert(token);

        // should have attempted to insert a authCodeToken
        ArgumentCaptor<AuthCodeToken> authCodeTokenCaptor = ArgumentCaptor.forClass(AuthCodeToken.class);
        verify(mockAuthCodeTokenRepository).insert(authCodeTokenCaptor.capture());

        AuthCodeToken actualACT = authCodeTokenCaptor.getValue();
        assertThat(actualACT.getId(), is(notNullValue()));
        assertThat(actualACT.getTokenId(), is(token.getUuid()));
        assertThat(actualACT.getAuthCodeId(), is(authCodeId));

        // should have rejected tokens and auth code.
        verify(mockTokenRepository).revokeByAuthCodeId(authCodeId);
        verify(mockAuthCodeRepository).revokeById(authCodeId);

        // should never insert anything else!
        verify(mockResourceOwnerTokenRepository, never()).insert(any(ResourceOwnerToken.class));
        verify(mockTokenScopeRepository, never()).insert(any(TokenScope.class));
    }
}