package net.tokensmith.authorization.oauth2.grant.redirect.code.token;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.oauth2.grant.token.MakeRefreshToken;
import net.tokensmith.authorization.persistence.entity.RefreshToken;
import net.tokensmith.authorization.persistence.entity.Token;
import net.tokensmith.authorization.security.ciphers.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 10/4/16.
 */
public class MakeRefreshTokenTest {

    @Mock
    private HashTextStaticSalt mockHashText;
    private MakeRefreshToken subject;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.initMocks(this);
        subject = new MakeRefreshToken(mockHashText);
    }

    @Test
    public void runShouldBeOk() throws Exception {
        String accessToken = "access-token";
        UUID clientId = UUID.randomUUID();
        Token token = FixtureFactory.makeOpenIdToken(accessToken, clientId, new ArrayList<>());
        String plainTextToken = "token";
        String hashedToken = "hashedToken";
        when(mockHashText.run(plainTextToken)).thenReturn(hashedToken);

        RefreshToken actual = subject.run(token, plainTextToken, 1209600L);

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual.getToken(), is(token));
        assertThat(actual.getAccessToken(), is(hashedToken.getBytes()));
        assertThat(actual.getExpiresAt(), is(notNullValue()));
    }

}