package org.rootservices.authorization.grant.code.authenticate;

import org.junit.Before;
import org.junit.Test;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 4/20/15.
 */
public class MakeAuthRequestImplTest {

    private MakeAuthRequest subject;

    @Before
    public void setUp() {
        subject = new MakeAuthRequestImpl();
    }
    @Test
    public void testRun() throws Exception {
        UUID clientUUID = UUID.randomUUID();
        ResponseType responseType = ResponseType.CODE;
        Optional<URI> redirectURI = Optional.of(new URI("https://rootservices.org"));
        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientUUID);
        authRequest.setResponseType(responseType);
        authRequest.setRedirectURI(redirectURI);
        authRequest.setScopes(scopes);

        UUID authCodeUUID = UUID.randomUUID();

        org.rootservices.authorization.persistence.entity.AuthRequest actual = subject.run(authCodeUUID, authRequest);
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getResponseType()).isEqualTo(responseType);
        assertThat(actual.getRedirectURI()).isEqualTo(redirectURI);
        assertThat(actual.getAuthCodeUUID()).isEqualTo(authCodeUUID);

    }
}