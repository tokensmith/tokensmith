package net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 5/16/16.
 */
public class CompareClientToAuthRequestTokenResponseTypeTest {
    @Mock
    private ClientRepository mockClientRepository;

    private CompareClientToAuthRequest subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ComparePublicClientToAuthRequest(mockClientRepository);
    }

    public AuthRequest makeAuthRequestFromClient(Client client) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getId());

        List<String> responseTypes = new ArrayList<>();
        for(ResponseType responseType: client.getResponseTypes()) {
            responseTypes.add(responseType.getName());
        }
        authRequest.setResponseTypes(responseTypes);

        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        return authRequest;
    }

    @Test
    public void shouldBeOk() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {
        Client client = FixtureFactory.makeCodeClientWithScopes();

        AuthRequest authRequest = makeAuthRequestFromClient(client);
        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        boolean isValid = subject.run(authRequest);
        assertThat(isValid, is(true));
    }

    @Test
    public void runClientNotFoundShouldThrowInformResourceOwnerException() throws RecordNotFoundException {
        UUID uuid = UUID.randomUUID();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);

        when(mockClientRepository.getById(authRequest.getClientId())).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(authRequest);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void responseTypeMismatchShouldThrowInformClientException() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithScopes();

        AuthRequest authRequest = makeAuthRequestFromClient(client);
        client.getResponseTypes().get(0).setName("TOKEN");

        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_MISMATCH.getCode()));
            assertTrue(e.getError().equals("unauthorized_client"));
            assertTrue(e.getRedirectURI().equals(client.getRedirectURI()));
        }
    }

    @Test
    public void redirectUriMismatchShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithScopes();

        Optional<URI> requestRedirectUri = Optional.of(new URI("https://tokensmith.net/mismatch"));
        AuthRequest authRequest = makeAuthRequestFromClient(client);
        authRequest.setRedirectURI(requestRedirectUri);

        when(mockClientRepository.getById(
                authRequest.getClientId())).thenReturn(client);

        try {
            subject.run(authRequest);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void authRequestInvalidScopeShouldThrowInformClientException() throws URISyntaxException, RecordNotFoundException {
        Client client = FixtureFactory.makeCodeClientWithScopes();

        AuthRequest authRequest = makeAuthRequestFromClient(client);
        authRequest.getScopes().add("invalid-scope");

        when(mockClientRepository.getById(
                authRequest.getClientId())
        ).thenReturn(client);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
            assertTrue(e.getError().equals("invalid_scope"));
            assertTrue(e.getRedirectURI().equals(client.getRedirectURI()));
        }
    }

}