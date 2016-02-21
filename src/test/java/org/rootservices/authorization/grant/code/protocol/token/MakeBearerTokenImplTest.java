package org.rootservices.authorization.grant.code.protocol.token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.HashTextStaticSalt;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
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
        UUID authCodeUUID = UUID.randomUUID();
        String plainTextToken = "token";
        String hashedToken = "hashedToken";
        when(mockHashText.run(plainTextToken)).thenReturn(hashedToken);

        Token actual = subject.run(authCodeUUID, plainTextToken);
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getAuthCodeUUID()).isEqualTo(authCodeUUID);
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getToken()).isEqualTo(hashedToken.getBytes());
        assertThat(actual.getExpiresAt()).isNotNull();
    }

    @Test
    public void getTokenType() {
        assertThat(subject.getTokenType()).isEqualTo(TokenType.BEARER);
    }

    @Test
    public void getSecondsToExpiration() {
        assertThat(subject.getSecondsToExpiration()).isEqualTo(3600);
    }
}