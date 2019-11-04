package net.tokensmith.authorization.openId.grant.redirect.code.authorization.request;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/30/15.
 */
public class CompareConfidentialClientToOpenIdAuthRequestTest {
    @Mock
    private ConfidentialClientRepository mockConfidentialClientRepository;

    private CompareConfidentialClientToOpenIdAuthRequest subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CompareConfidentialClientToOpenIdAuthRequest(mockConfidentialClientRepository);
    }

    @Test
    public void shouldBeOk() throws URISyntaxException, RecordNotFoundException, InformClientException, InformResourceOwnerException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getId());

        List<String> requestResponseTypes = new ArrayList<>();
        for(ResponseType rt: client.getResponseTypes()) {
            requestResponseTypes.add(rt.getName());
        }
        openIdAuthRequest.setResponseTypes(requestResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                openIdAuthRequest.getClientId())
        ).thenReturn(confidentialClient);

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

        when(mockConfidentialClientRepository.getByClientId(
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
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getId());

        List<String> requestedResponseTypes = new ArrayList<>();
        requestedResponseTypes.add("TOKEN");
        openIdAuthRequest.setResponseTypes(requestedResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());

        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        Optional<String> state = Optional.of("some-state");
        openIdAuthRequest.setState(state);

        when(mockConfidentialClientRepository.getByClientId(
                openIdAuthRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.RESPONSE_TYPE_MISMATCH.getCode()));
            assertThat(e.getError(), is("unauthorized_client"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
            assertThat(e.getState(), is(openIdAuthRequest.getState()));
        }
    }

    @Test
    public void redirectUriMismatchShouldThrowInformResourceOwnerException() throws RecordNotFoundException, URISyntaxException {
        Client client = FixtureFactory.makeCodeClientWithOpenIdScopes();
        ConfidentialClient confidentialClient = FixtureFactory.makeConfidentialClient(client);

        URI requestRedirectUri = new URI("https://rootservices.org/mismatch");

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        openIdAuthRequest.setClientId(client.getId());

        List<String> requestedResponseTypes = new ArrayList<>();
        requestedResponseTypes.add("CODE");
        openIdAuthRequest.setResponseTypes(requestedResponseTypes);

        openIdAuthRequest.setRedirectURI(requestRedirectUri);
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        openIdAuthRequest.setScopes(scopes);

        when(mockConfidentialClientRepository.getByClientId(
                openIdAuthRequest.getClientId())
        ).thenReturn(confidentialClient);

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
        openIdAuthRequest.setClientId(client.getId());

        List<String> requestResponseTypes = new ArrayList<>();
        for(ResponseType rt: client.getResponseTypes()) {
            requestResponseTypes.add(rt.getName());
        }
        openIdAuthRequest.setResponseTypes(requestResponseTypes);

        openIdAuthRequest.setRedirectURI(client.getRedirectURI());

        List<String> scopes = new ArrayList<>();
        scopes.add("invalid-scope");
        openIdAuthRequest.setScopes(scopes);

        Optional<String> state = Optional.of("some-state");
        openIdAuthRequest.setState(state);

        when(mockConfidentialClientRepository.getByClientId(
                openIdAuthRequest.getClientId())
        ).thenReturn(confidentialClient);

        try {
            subject.run(openIdAuthRequest);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
            assertThat(e.getError(), is("invalid_scope"));
            assertThat(e.getRedirectURI(), is(client.getRedirectURI()));
            assertThat(e.getState(), is(openIdAuthRequest.getState()));
        }
    }
}