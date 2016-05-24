package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.CompareClientToAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
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
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(client.getResponseType());
        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        boolean isValid = subject.run(authRequest);
        assertThat(isValid).isTrue();
    }

    @Test
    public void runClientNotFound() throws RecordNotFoundException {
        UUID uuid = UUID.randomUUID();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.CODE);
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
            assertThat(e.getCode()).isEqualTo(ErrorCode.CLIENT_NOT_FOUND.getCode());
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void runUnAuthorizedResponseType() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        client.setResponseType(ResponseType.TOKEN);
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(ResponseType.CODE);
        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode()).isEqualTo(ErrorCode.RESPONSE_TYPE_MISMATCH.getCode());
            assertThat(e.getError().equals("unauthorized_client"));
            assertThat(e.getRedirectURI().equals(client.getRedirectURI()));
        }
    }

    @Test
    public void runRedirectUriMismatch() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        client.setRedirectURI(new URI("https://rootservices.org/mismatch"));
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        Optional<URI> expectedRedirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(ResponseType.CODE);
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
            assertThat(e.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void runInvalidScope() throws URISyntaxException, RecordNotFoundException {
        Client client = FixtureFactory.makeCodeClientWithScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(client.getResponseType());
        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("unsupported-scope");
        authRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                authRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(authRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode()).isEqualTo(ErrorCode.SCOPES_NOT_SUPPORTED.getCode());
            assertThat(e.getError().equals("invalid-scope"));
            assertThat(e.getRedirectURI().equals(client.getRedirectURI()));
        }
    }
}