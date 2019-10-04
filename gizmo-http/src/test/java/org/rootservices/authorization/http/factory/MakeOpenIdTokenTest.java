package org.rootservices.authorization.http.factory;

import helpers.category.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.openId.identity.MakeCodeGrantIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.http.factory.exception.TokenException;
import org.rootservices.authorization.http.response.OpenIdToken;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.openId.identity.exception.ResourceOwnerNotFoundException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 2/20/16.
 */
@Category(UnitTests.class)
public class MakeOpenIdTokenTest {
    private MakeOpenIdToken subject;
    @Mock
    private MakeCodeGrantIdentityToken mockBuildIdentityToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new MakeOpenIdToken(mockBuildIdentityToken);
    }

    public TokenResponse makeTokenResponse() {
        TokenResponse tr = new TokenResponse();
        tr.setTokenType(TokenType.BEARER);
        tr.setExpiresIn(3600L);
        tr.setAccessToken("accessToken");
        tr.setRefreshAccessToken("refreshAccessToken");

        TokenClaims tc = new TokenClaims();
        tr.setTokenClaims(tc);
        return tr;
    }

    @Test
    public void makeShouldMakeOpenIdToken() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenReturn("idToken");

        Token actual = subject.make(tokenResponse);

        assertThat(actual.getAccessToken(), is("accessToken"));
        assertThat(actual.getRefreshToken(), is("refreshAccessToken"));
        assertThat(actual.getTokenType(), is(org.rootservices.authorization.http.response.TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(((OpenIdToken) actual).getIdToken(), is("idToken"));
    }

    @Test(expected = TokenException.class)
    public void makeResourceOwnerNotFoundShouldThrowTokenException() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenThrow(TokenException.class);

        subject.make(tokenResponse);
    }

    @Test(expected = TokenException.class)
    public void makeIdTokenExceptionThrowTokenException() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenThrow(IdTokenException.class);

        subject.make(tokenResponse);
    }

    @Test(expected = TokenException.class)
    public void makeKeyNotFoundExceptionThrowTokenException() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenThrow(KeyNotFoundException.class);

        subject.make(tokenResponse);
    }

    @Test(expected = TokenException.class)
    public void makeProfileNotFoundExceptionThrowTokenException() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenThrow(ProfileNotFoundException.class);

        subject.make(tokenResponse);
    }

    @Test(expected = TokenException.class)
    public void makeResourceOwnerNotFoundExceptionThrowTokenException() throws Exception {
        TokenResponse tokenResponse = makeTokenResponse();
        when(mockBuildIdentityToken.make("accessToken", tokenResponse.getTokenClaims())).thenThrow(ResourceOwnerNotFoundException.class);

        subject.make(tokenResponse);
    }
}