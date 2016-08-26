package org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.ComparePublicClientToOpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 8/12/16.
 */
public class ComparePublicClientToOpenIdAuthRequestTest {
    @Mock
    private ClientRepository mockClientRepository;

    private ComparePublicClientToOpenIdAuthRequest subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new ComparePublicClientToOpenIdAuthRequest(mockClientRepository);
    }

    @Test
    public void shouldBeOk() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {
        Client client = FixtureFactory.makeTokenClientWithOpenIdScopes();

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getUuid());

        List<String> requestResponseTypes = new ArrayList<>();
        for(ResponseType rt: client.getResponseTypes()) {
            requestResponseTypes.add(rt.getName());
        }
        openIdAuthRequest.setResponseTypes(requestResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockClientRepository.getById(
                openIdAuthRequest.getClientId())
        ).thenReturn(client);

        boolean isValid = subject.run(openIdAuthRequest);
        assertThat(isValid, is(true));
    }

    @Test
    public void runClientNotFoundShouldThrowInformResourceOwnerException() throws RecordNotFoundException {
        UUID uuid = UUID.randomUUID();

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(uuid);

        List<String> requestedResponseTypes = new ArrayList<>();
        requestedResponseTypes.add("CODE");
        openIdAuthRequest.setResponseTypes(requestedResponseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockClientRepository.getById(
                openIdAuthRequest.getClientId())
        ).thenThrow(RecordNotFoundException.class);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void responseTypeMismatchShouldThrowInformClientException() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getUuid());

        List<String> requestedResponseTypes = new ArrayList<>();
        requestedResponseTypes.add("TOKEN");
        openIdAuthRequest.setResponseTypes(requestedResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockClientRepository.getById(
                openIdAuthRequest.getClientId())
        ).thenReturn(client);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_MISMATCH.getCode()));
            assertThat(e.getError(), is("unauthorized_client"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
        }
    }

    @Test
    public void redirectUriMismatchShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();

        URI requestRedirectUri = new URI("https://rootservices.org/mismatch");

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getUuid());

        List<String> requestedResponseTypes = new ArrayList<>();
        requestedResponseTypes.add("CODE");
        openIdAuthRequest.setResponseTypes(requestedResponseTypes);

        openIdAuthRequest.setRedirectURI(requestRedirectUri);
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockClientRepository.getById(
                openIdAuthRequest.getClientId())
        ).thenReturn(client);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void authRequestInvalidScopeShouldThrowInformClientException() throws URISyntaxException, RecordNotFoundException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getUuid());

        List<String> requestResponseTypes = new ArrayList<>();
        for(ResponseType rt: client.getResponseTypes()) {
            requestResponseTypes.add(rt.getName());
        }
        openIdAuthRequest.setResponseTypes(requestResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());
        List<String> scopes = new ArrayList<>();
        scopes.add("invalid-scope");
        openIdAuthRequest.setScopes(scopes);

        when(mockClientRepository.getById(
                openIdAuthRequest.getClientId())
        ).thenReturn(client);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
            assertThat(e.getError(), is("invalid_scope"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
        }
    }
}