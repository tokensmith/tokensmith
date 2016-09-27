package org.rootservices.authorization.oauth2.grant.password;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeBearerToken;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;

import java.util.List;

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
    private MakeBearerToken mockMakeBearerToken;
    @Mock
    private TokenRepository mockTokenRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;
    @Mock
    private TokenScopeRepository mockTokenScopeRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenPasswordGrant(
                mockMakeBearerToken,
                mockTokenRepository,
                mockResourceOwnerTokenRepository,
                mockTokenScopeRepository
        );
    }

    @Test
    public void runShouldBeOk() throws Exception {
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String plainTextAccessToken = "token";

        List<Scope> scopes = FixtureFactory.makeScopes();

        Token token = FixtureFactory.makeOpenIdToken();
        ArgumentCaptor<TokenScope> tokenScopeCaptor = ArgumentCaptor.forClass(TokenScope.class);
        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);

        when(mockMakeBearerToken.run(plainTextAccessToken)).thenReturn(token);

        Token actualToken = subject.run(resourceOwner.getUuid(), plainTextAccessToken, scopes);

        assertThat(actualToken, is(notNullValue()));
        assertThat(actualToken, is(token));
        assertThat(actualToken.getGrantType(), is(GrantType.PASSWORD));

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
        assertThat(actualRot.getResourceOwner().getUuid(), is(resourceOwner.getUuid()));
    }
}