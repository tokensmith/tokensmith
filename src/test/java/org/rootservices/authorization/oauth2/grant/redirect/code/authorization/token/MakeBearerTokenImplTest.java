package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeBearerTokenImpl;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeToken;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 6/2/15.
 */
public class MakeBearerTokenImplTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private MakeToken subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new MakeBearerTokenImpl(mockHashText);
    }

    @Test
    public void testRun() throws Exception {
        String plainTextToken = "token";
        String hashedToken = "hashedToken";
        when(mockHashText.run(plainTextToken)).thenReturn(hashedToken);

        Token actual = subject.run(plainTextToken);
        assertThat(actual.getUuid(), is(notNullValue()));

        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken(), is(hashedToken.getBytes()));
        assertThat(actual.getExpiresAt(), is(notNullValue()));
        assertThat(actual.getSecondsToExpiration(), is(subject.getSecondsToExpiration()));
    }

    @Test
    public void getTokenTypeShouldBeBearer() {
        assertThat(subject.getTokenType(), is(TokenType.BEARER));
    }

    @Test
    public void getSecondsToExpirationShouldBeOk() {
        assertThat(subject.getSecondsToExpiration(), is(3600L));
    }
}