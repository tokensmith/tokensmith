package org.rootservices.authorization.http.factory;

import helpers.category.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.rootservices.authorization.http.factory.exception.TokenException;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.http.response.TokenType;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@Category(UnitTests.class)
public class MakeOAuthTokenTest {
    MakeOAuthToken subject;

    @Before
    public void setUp() {
        subject = new MakeOAuthToken();
    }

    public TokenResponse makeTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setTokenType(org.rootservices.authorization.oauth2.grant.token.entity.TokenType.BEARER);
        tokenResponse.setExpiresIn(3600L);
        tokenResponse.setAccessToken("accessToken");
        tokenResponse.setRefreshAccessToken("refreshAccessToken");
        return tokenResponse;
    }

    @Test
    public void makeShouldMakeOAuthToken() throws TokenException {
        TokenResponse tokenResponse = makeTokenResponse();
        Token actual = subject.make(tokenResponse);

        assertThat(actual.getAccessToken(), is("accessToken"));
        assertThat(actual.getRefreshToken(), is("refreshAccessToken"));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
    }
}