package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


/**
 * Created by tommackenzie on 4/29/15.
 */
public class AuthResponseFactoryTest {

    @Mock
    private ClientRepository clientRepository;

    private AuthResponseFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new AuthResponseFactory(clientRepository);
    }

    @Test
    public void redirectUriIsPresent() throws InformResourceOwnerException, URISyntaxException {
        UUID clientUUID = UUID.randomUUID();
        String authCode = "authorization-code";
        Optional<String> state = Optional.of("csrf");
        Optional<URI> redirectUri = Optional.of(new URI("https://rootservices.org"));

        AuthResponse actual = subject.makeAuthResponse(
                clientUUID,
                authCode,
                state,
                redirectUri
        );

        assertThat(actual.getState(), is(state));
        assertThat(actual.getRedirectUri(), is(redirectUri.get()));
        assertThat(actual.getCode(), is(authCode));
    }

    @Test
    public void redirectUriIsNotPresentClientNotFound() throws URISyntaxException, RecordNotFoundException {
        UUID clientUUID = UUID.randomUUID();
        String authCode = "authorization-code";
        Optional<String> state = Optional.of("csrf");
        Optional<URI> redirectUri = Optional.empty();

        when(clientRepository.getById(clientUUID)).thenThrow(RecordNotFoundException.class);

        AuthResponse actual = null;
        try {
            actual = subject.makeAuthResponse(
                    clientUUID,
                    authCode,
                    state,
                    redirectUri
            );
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
        }

        assertThat(actual, is(nullValue()));
    }
}