package org.rootservices.persistence.entity;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
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
        subject.setUuid(uuid);

        assertThat(subject.getUuid()).isEqualTo(uuid);
    }

    @Test
    public void responseType() {
        ResponseType rt = ResponseType.CODE;
        subject.setResponseType(rt);

        assertThat(subject.getResponseType()).isEqualTo(rt);
    }

    @Test
    public void redirectURI() throws URISyntaxException {
        URI redirectUri = new URI("https://rootservices.org");
        subject.setRedirectURI(redirectUri);

        assertThat(subject.getRedirectURI()).isEqualTo(redirectUri);
    }
}
