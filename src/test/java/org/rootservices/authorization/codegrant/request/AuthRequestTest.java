package org.rootservices.authorization.codegrant.request;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuthRequestTest {

    AuthRequest subject;

    @Before
    public void setUp() {
        subject = new AuthRequest();
    }

    @Test
    public void clientId() throws Exception {
        UUID uuid = UUID.randomUUID();
        subject.setClientId(uuid);

        assertThat(subject.getClientId()).isEqualTo(uuid);
    }

    @Test
    public void redirectURI() throws Exception {
        URI redirectUri = new URI("https://rootservices.org");
        subject.setRedirectURI(redirectUri);

        assertThat(subject.getRedirectURI()).isEqualTo(redirectUri);
    }
}