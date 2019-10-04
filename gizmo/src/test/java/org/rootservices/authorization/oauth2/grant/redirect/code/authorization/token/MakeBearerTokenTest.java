package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 6/2/15.
 */
public class MakeBearerTokenTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private MakeBearerToken subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new MakeBearerToken(mockHashText);
    }

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        String plainTextToken = "token";
        String hashedToken = "hashedToken";
        when(mockHashText.run(plainTextToken)).thenReturn(hashedToken);

        Token actual = subject.run(clientId, plainTextToken, 3600L);
        assertThat(actual.getId(), is(notNullValue()));

        assertThat(actual.getClientId(), is(clientId));
        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken(), is(hashedToken.getBytes()));
        assertThat(actual.getExpiresAt(), is(notNullValue()));
        assertThat(actual.getSecondsToExpiration(), is(3600L));
        assertThat(actual.getTokenScopes(), is(notNullValue()));
        assertThat(actual.getTokenScopes().size(), is(0));
        assertThat(actual.getAudience(), is(notNullValue()));
        assertThat(actual.getAudience().size(), is(0));
    }

    @Test
    public void getTokenTypeShouldBeBearer() {
        assertThat(subject.getTokenType(), is(TokenType.BEARER));
    }
}