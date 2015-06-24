package org.rootservices.authorization.grant.code.protocol.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateAuthRequestImplTest {

    @Mock
    private ClientRepository mockClientRepository;

    private ValidateAuthRequest subject;

    @Before
    public void setUp() {
        subject = new ValidateAuthRequestImpl(mockClientRepository);
    }

    @Test
    public void run() throws InformResourceOwnerException, InformClientException, RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeClientWithScopes();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(client.getResponseType());
        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenReturn(client);

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

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenThrow(
                RecordNotFoundException.class
        );

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
        UUID uuid = UUID.randomUUID();
        URI expectedRedirectURI = new URI("https://rootservices.org");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.CODE);
        authRequest.setRedirectURI(Optional.ofNullable(expectedRedirectURI));
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        Client client = new Client();
        client.setUuid(uuid);
        client.setResponseType(ResponseType.TOKEN);
        client.setRedirectURI(expectedRedirectURI);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenReturn(client);

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
        UUID uuid = UUID.randomUUID();

        Optional<URI> expectedRedirectURI = Optional.ofNullable(new URI("https://rootservices.org"));
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(uuid);
        authRequest.setResponseType(ResponseType.CODE);
        authRequest.setRedirectURI(expectedRedirectURI);
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        authRequest.setScopes(scopes);

        URI actualRedirectURI = new URI("https://rootservices.org/mismatch");
        Client client = new Client();
        client.setUuid(uuid);
        client.setResponseType(ResponseType.CODE);
        client.setRedirectURI(actualRedirectURI);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenReturn(client);

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
        Client client = FixtureFactory.makeClientWithScopes();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(client.getUuid());
        authRequest.setResponseType(client.getResponseType());
        authRequest.setRedirectURI(Optional.ofNullable(client.getRedirectURI()));
        List<String> scopes = new ArrayList<>();
        scopes.add("unsupported-scope");
        authRequest.setScopes(scopes);

        when(mockClientRepository.getByUUID(authRequest.getClientId())).thenReturn(client);

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