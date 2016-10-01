package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeBearerToken;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 6/24/16.
 */
public class IssueTokenImplicitGrantTest {
    private IssueTokenImplicitGrant subject;
    @Mock
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenImplicitGrant(mockMakeBearerToken, mockTokenRepository, mockScopeRepository, mockTokenScopeRepository, mockResourceOwnerTokenRepository);
    }

    @Test
    public void grantShouldReturnToken() throws Exception{
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String plainTextAccessToken = "token";
        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("profile");

        Token token = FixtureFactory.makeOpenIdToken();
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);

        when(mockMakeBearerToken.run(plainTextAccessToken)).thenReturn(token);

        List<Scope> scopes = FixtureFactory.makeScopes();
        when(mockScopeRepository.findByNames(scopeNames)).thenReturn(scopes);

        Token actualToken = subject.run(resourceOwner, scopeNames, plainTextAccessToken);

        assertThat(actualToken, is(notNullValue()));
        assertThat(actualToken, is(token));
        assertThat(actualToken.getGrantType(), is(GrantType.TOKEN));

        assertThat(actualToken.getTokenScopes().size(), is(1));
        assertThat(actualToken.getTokenScopes().get(0).getId(), is(notNullValue()));
        assertThat(actualToken.getTokenScopes().get(0).getTokenId(), is(token.getUuid()));
        assertThat(actualToken.getTokenScopes().get(0).getScope(), is(scopes.get(0)));

        verify(mockTokenRepository, times(1)).insert(token);

        verify(mockTokenScopeRepository, times(1)).insert(tokenScopeCaptor.capture());
        TokenScope actualTokenScope = tokenScopeCaptor.getValue();
        assertThat(actualTokenScope.getId(), is(notNullValue()));
        assertThat(actualTokenScope.getTokenId(), is(token.getUuid()));
        assertThat(actualTokenScope.getScope(), is(scopes.get(0)));

        verify(mockResourceOwnerTokenRepository, times(1)).insert(resourceOwnerTokenCaptor.capture());
        ResourceOwnerToken actualRot = resourceOwnerTokenCaptor.getValue();
        assertThat(actualRot.getId(), is(notNullValue()));
        assertThat(actualRot.getToken(), is(token));
        assertThat(actualRot.getResourceOwner(), is(resourceOwner));

    }
}