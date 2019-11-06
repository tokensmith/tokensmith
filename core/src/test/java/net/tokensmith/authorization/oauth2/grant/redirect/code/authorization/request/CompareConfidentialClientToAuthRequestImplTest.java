package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.CompareClientToAuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ConfidentialClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class CompareConfidentialClientToAuthRequestImplTest {

    @Mock
    private ConfidentialClientRepository mockConfidentialClientRepository;

    private CompareClientToAuthRequest subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CompareConfidentialClientToAuthRequest(mockConfidentialClientRepository);
    }

    @Test
    public void run() throws InformResourceOwnerException, InformClientException, RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getId());

        List<String> resonseTypes = new ArrayList<>();
        for(ResponseType rt: client.getResponseTypes()) {
            resonseTypes.add(rt.getName());
        }
        authRequest.setResponseTypes(resonseTypes);

        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        boolean isValid = subject.run(authRequest);
        assertThat(isValid, is(true));
    }

    @Test
    public void runClientNotFound() throws RecordNotFoundException {
        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add("CODE");
        authRequest.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenThrow(RecordNotFoundException.class);

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
    public void runUnAuthorizedResponseType() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();

        client.getResponseTypes().get(0).setName("TOKEN");
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getId());

        authRequest.setResponseTypes(new ArrayList<>());
        authRequest.getResponseTypes().add("CODE");

        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        Optional<String> state = Optional.of("some-state");
        authRequest.setState(state);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_MISMATCH.getCode()));
            assertThat(e.getError(), is("unauthorized_client"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
            assertThat(e.getState(), is(authRequest.getState()));
        }
    }

    @Test
    public void runRedirectUriMismatch() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        client.setRedirectURI(new URI("https://rootservices.org/mismatch"));
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        Optional<URI> expectedRedirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getId());

        authRequest.setResponseTypes(new ArrayList<>());
        authRequest.getResponseTypes().add("CODE");

        authRequest.setRedirectURI(expectedRedirectURI);
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

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
    public void runInvalidScope() throws URISyntaxException, RecordNotFoundException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getId());

        authRequest.setResponseTypes(new ArrayList<>());
        for(ResponseType responseType: client.getResponseTypes()) {
            authRequest.getResponseTypes().add(responseType.getName());
        }

        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));

        List<String> scopes = new ArrayList<>();
        scopes.add("unsupported-scope");
        authRequest.setScopes(scopes);

        Optional<String> state = Optional.of("some-state");
        authRequest.setState(state);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
            assertThat(e.getError(), is("invalid_scope"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
            assertThat(e.getState(), is(authRequest.getState()));
        }
    }
}