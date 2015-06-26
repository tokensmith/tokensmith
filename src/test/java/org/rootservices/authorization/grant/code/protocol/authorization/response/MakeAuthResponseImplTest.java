package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


/**
 * Created by tommackenzie on 4/29/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MakeAuthResponseImplTest {

    @Mock
    private ClientRepository clientRepository;

    private MakeAuthResponse subject;

    @Before
    public void setUp() {
        subject = new MakeAuthResponseImpl(clientRepository);
    }

    @Test
    public void redirectUriIsPresent() throws InformResourceOwnerException, URISyntaxException {
        UUID clientUUID = UUID.randomUUID();
        String authCode = "authorization-code";
        Optional<String> state = Optional.of("csrf");
        Optional<URI> redirectUri = Optional.of(new URI("https://rootservices.org"));

        AuthResponse actual = subject.run(
                clientUUID,
                authCode,
                state,
                redirectUri
        );

        assertThat(actual.getState()).isEqualTo(state);
        assertThat(actual.getRedirectUri()).isEqualTo(redirectUri.get());
        assertThat(actual.getCode()).isEqualTo(authCode);
    }

    @Test
    public void redirectUriIsNotPresentClientNotFound() throws URISyntaxException, RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String authCode = "authorization-code";
        Optional<String> state = Optional.of("csrf");
        Optional<URI> redirectUri = Optional.empty();

        when(clientRepository.getByUUID(clientUUID)).thenThrow(RecordNotFoundException.class);

        AuthResponse actual = null;
        try {
            actual = subject.run(
                    clientUUID,
                    authCode,
                    state,
                    redirectUri
            );
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode()).isEqualTo(ErrorCode.CLIENT_NOT_FOUND.getCode());
            assertThat(e.getDomainCause()).isInstanceOf(RecordNotFoundException.class);
        }

        assertThat(actual).isNull();
    }
}