package net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.request.ValidateImplicitGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.repository.entity.*;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;


import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 6/23/16.
 */
public class RequestAccessTokenTest {

    private RequestAccessToken subject;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private ValidateImplicitGrant mockValidateImplicitGrant;
    @Mock
    private IssueTokenImplicitGrant mockIssueTokenImplicitGrant;
    @Mock
    private ClientRepository mockClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestAccessToken(mockLoginResourceOwner, mockValidateImplicitGrant, mockIssueTokenImplicitGrant, mockClientRepository);
    }

    @Test
    public void requestTokenShouldReturnToken() throws Exception {

        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;

        Client client = FixtureFactory.makeTokenClientWithScopes();
        Map<String, List<String>> parameters = FixtureFactory.makeOAuthParameters(client.getId(), "TOKEN");
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(client.getId(), "TOKEN");

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        List<Client> audience = FixtureFactory.makeAudience(client);
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph(client.getId(), audience);

        when(mockValidateImplicitGrant.run(parameters)).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);
        when(mockClientRepository.getById(client.getId())).thenReturn(client);
        when(mockIssueTokenImplicitGrant.run(client.getId(), resourceOwner, authRequest.getScopes(), audience)).thenReturn(tokenGraph);

        ImplicitAccessToken actual = subject.requestToken(userName, password, parameters);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(FixtureFactory.makeSecureRedirectUri()));
        assertThat(actual.getAccessToken(), is(tokenGraph.getPlainTextAccessToken()));
        assertThat(actual.getScope(), is(Optional.of("profile")));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
    }

    @Test
    public void requestWhenServerErrorShouldThrowInformClientException() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;

        UUID clientId = UUID.randomUUID();
        Map<String, List<String>> parameters = FixtureFactory.makeOAuthParameters(clientId, "TOKEN");
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "TOKEN");

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Client client = FixtureFactory.makeTokenClientWithScopes();
        List<Client> audience = new ArrayList<>();
        audience.add(client);

        when(mockValidateImplicitGrant.run(parameters)).thenReturn(authRequest);
        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);

        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        ServerException se = new ServerException("test", null);
        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, authRequest.getScopes(), audience))
                .thenThrow(se);

        InformClientException actual = null;
        try {
            subject.requestToken(userName, password, parameters);
        } catch (InformClientException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to issue token"));
        assertThat(actual.getError(), is("server_error"));
        assertThat(actual.getDescription(), is(ErrorCode.SERVER_ERROR.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.SERVER_ERROR.getCode()));
        assertThat(actual.getRedirectURI(), is(client.getRedirectURI()));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
        assertThat(actual.getCause(), is(se));

    }

    @Test
    public void requestTokenWithNoRedirectUriShouldReturnToken() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;

        UUID clientId = UUID.randomUUID();
        Map<String, List<String>> parameters = FixtureFactory.makeOAuthParameters(clientId, "TOKEN");
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "TOKEN");
        authRequest.setRedirectURI(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        Client client = FixtureFactory.makeTokenClientWithScopes();
        List<Client> audience = new ArrayList<>();
        audience.add(client);

        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph(clientId, audience);

        when(mockValidateImplicitGrant.run(parameters)).thenReturn(authRequest);
        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);
        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, authRequest.getScopes(), audience)).thenReturn(tokenGraph);
        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        ImplicitAccessToken actual = subject.requestToken(userName, password, parameters);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(client.getRedirectURI()));
        assertThat(actual.getAccessToken(), is(tokenGraph.getPlainTextAccessToken()));
        assertThat(actual.getScope(), is(Optional.of("profile")));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("some-state"));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        // should fetch client for redirect uri.
        verify(mockClientRepository, times(2)).getById(authRequest.getClientId());
    }


    @Test
    public void requestTokenWithNoRedirectUriClientNotFoundShouldThrowInformResourceOwnerException() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;

        UUID clientId = UUID.randomUUID();
        Map<String, List<String>> parameters = FixtureFactory.makeOAuthParameters(clientId, "TOKEN");
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "TOKEN");

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        List<Client> audience = FixtureFactory.makeAudience(clientId);
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph(clientId, audience);

        when(mockValidateImplicitGrant.run(parameters)).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);

        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, authRequest.getScopes(), audience)).thenReturn(tokenGraph);

        when(mockClientRepository.getById(authRequest.getClientId())).thenThrow(RecordNotFoundException.class);

        try {
            ImplicitAccessToken actual = subject.requestToken(userName, password, parameters);
            fail("Expected to throw, InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getMessage(), is(ErrorCode.CLIENT_NOT_FOUND.getDescription()));
        }
    }
}