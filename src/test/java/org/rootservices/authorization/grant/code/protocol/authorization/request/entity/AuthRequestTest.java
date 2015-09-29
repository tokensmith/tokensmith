package org.rootservices.authorization.grant.code.protocol.authorization.request.entity;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<URI> redirectUri = Optional.ofNullable(new URI("https://rootservices.org"));
        subject.setRedirectURI(redirectUri);

        assertThat(subject.getRedirectURI()).isEqualTo(redirectUri);
    }

    @Test
    public void scopes() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        subject.setScopes(scopes);

        assertThat(subject.getScopes()).isEqualTo(scopes);
    }
}