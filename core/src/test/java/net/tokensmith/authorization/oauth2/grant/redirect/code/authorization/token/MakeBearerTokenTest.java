package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.token;

import net.tokensmith.authorization.oauth2.grant.token.MakeBearerToken;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.Token;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 6/2/15.
 */
public class MakeBearerTokenTest {

    @Mock
    private HashToken mockHashToken;
    private MakeBearerToken subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new MakeBearerToken(mockHashToken);
    }

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        String plainTextToken = "token";
        String hashedToken = "hashedToken";
        when(mockHashToken.run(plainTextToken)).thenReturn(hashedToken);

        Token actual = subject.run(clientId, plainTextToken, 3600L);
        assertThat(actual.getId(), is(notNullValue()));

        assertThat(actual.getClientId(), is(clientId));
        assertThat(actual.getToken(), is(notNullValue()));
        assertThat(actual.getToken(), is(hashedToken));
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