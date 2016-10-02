package org.rootservices.authorization.persistence.entity;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 11/15/14.
 */
public class ClientTest {

    private Client subject;

    @Before
    public void setUp() {
        subject = new Client();
    }

    @Test
    public void UUID() {
        UUID uuid = UUID.randomUUID();
        subject.setId(uuid);

        assertThat(subject.getId()).isEqualTo(uuid);
    }

    @Test
    public void responseType() {
        List<ResponseType> rts = FixtureFactory.makeResponseTypes();
        subject.setResponseTypes(rts);

        assertThat(subject.getResponseTypes()).isEqualTo(rts);
    }

    @Test
    public void redirectURI() throws URISyntaxException {
        URI redirectUri = new URI("https://rootservices.org");
        subject.setRedirectURI(redirectUri);

        assertThat(subject.getRedirectURI()).isEqualTo(redirectUri);
    }
}
