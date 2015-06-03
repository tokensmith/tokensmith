package org.rootservices.authorization.grant.code.protocol.token;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.persistence.entity.Token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 6/2/15.
 */
public class MakeBearerTokenImplTest {

    private MakeToken subject;
    private MessageDigest digest;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        subject = new MakeBearerTokenImpl();
        digest = MessageDigest.getInstance("SHA-512");
    }

    @Test
    public void testRun() throws Exception {
        UUID authCodeUUID = UUID.randomUUID();
        String plainTextToken = "token";
        byte[] hashedToken = digest.digest(plainTextToken.getBytes());

        Token actual = subject.run(authCodeUUID, plainTextToken);
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getAuthCodeUUID()).isEqualTo(authCodeUUID);
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getToken()).isEqualTo(hashedToken);
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