package net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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

        assertThat(subject.getClientId(), is(uuid));
    }

    @Test
    public void redirectURI() throws Exception {
        Optional<URI> redirectUri = Optional.ofNullable(new URI("https://tokensmith.net"));
        subject.setRedirectURI(redirectUri);

        assertThat(subject.getRedirectURI(), is(redirectUri));
    }

    @Test
    public void scopes() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        subject.setScopes(scopes);

        assertThat(subject.getScopes(), is(scopes));
    }
}