package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.security.RandomString;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private ValidateParams mockValidateParamsTokenResponseType;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private IssueTokenImplicitGrant mockIssueTokenImplicitGrant;
    @Mock
    private ClientRepository mockClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestAccessToken(mockLoginResourceOwner, mockValidateParamsTokenResponseType, mockRandomString, mockIssueTokenImplicitGrant, mockClientRepository);
    }

    @Test
    public void requestTokenShouldReturnToken() throws Exception {

        UUID clientId = UUID.randomUUID();
        InputParams inputParams = FixtureFactory.makeEmptyGrantInput();
        inputParams.getClientIds().add(clientId.toString());
        inputParams.getRedirectUris().add(FixtureFactory.makeSecureRedirectUri().toString());
        inputParams.getScopes().add("profile");
        inputParams.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.of(FixtureFactory.makeSecureRedirectUri()));
        authRequest.setScopes(inputParams.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);

        when(mockValidateParamsTokenResponseType.run(
                inputParams.getClientIds(),
                inputParams.getResponseTypes(),
                inputParams.getRedirectUris(),
                inputParams.getScopes(),
                inputParams.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(inputParams.getUserName(), inputParams.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, inputParams.getScopes(), accessToken)).thenReturn(token);

        ImplicitAccessToken actual = subject.requestToken(inputParams);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(FixtureFactory.makeSecureRedirectUri()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getScope(), is(Optional.of("profile foo")));
        assertThat(actual.getState(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        verify(mockClientRepository, never()).getById(authRequest.getClientId());
    }

    @Test
    public void requestTokenWithNoRedirectUriShouldReturnToken() throws Exception {
        UUID clientId = UUID.randomUUID();
        InputParams inputParams = FixtureFactory.makeEmptyGrantInput();
        inputParams.getClientIds().add(clientId.toString());
        inputParams.getScopes().add("profile");
        inputParams.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.empty());
        authRequest.setScopes(inputParams.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        List<Scope> scopesToAddToToken = FixtureFactory.makeScopes();
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        Client client = FixtureFactory.makeTokenClientWithScopes();

        when(mockValidateParamsTokenResponseType.run(
                inputParams.getClientIds(),
                inputParams.getResponseTypes(),
                inputParams.getRedirectUris(),
                inputParams.getScopes(),
                inputParams.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(inputParams.getUserName(), inputParams.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, inputParams.getScopes(), accessToken)).thenReturn(token);

        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        ImplicitAccessToken actual = subject.requestToken(inputParams);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(client.getRedirectURI()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getScope(), is(Optional.of("profile foo")));
        assertThat(actual.getState(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        // should fetch client for redirect uri.
        verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
    }


    @Test
    public void requestTokenWithNoRedirectUriClientNotFoundShouldThrowInformResourceOwnerException() throws Exception {
        UUID clientId = UUID.randomUUID();
        InputParams inputParams = FixtureFactory.makeEmptyGrantInput();
        inputParams.getClientIds().add(clientId.toString());
        inputParams.getScopes().add("profile");
        inputParams.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.empty());
        authRequest.setScopes(inputParams.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        List<Scope> scopesToAddToToken = FixtureFactory.makeScopes();
        Token token = FixtureFactory.makeOpenIdToken(accessToken);

        when(mockValidateParamsTokenResponseType.run(
                inputParams.getClientIds(),
                inputParams.getResponseTypes(),
                inputParams.getRedirectUris(),
                inputParams.getScopes(),
                inputParams.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(inputParams.getUserName(), inputParams.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockIssueTokenImplicitGrant.run(authRequest.getClientId(), resourceOwner, inputParams.getScopes(), accessToken)).thenReturn(token);

        when(mockClientRepository.getById(authRequest.getClientId())).thenThrow(RecordNotFoundException.class);

        try {
            ImplicitAccessToken actual = subject.requestToken(inputParams);
            fail("Expected to throw, InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
            assertThat(e.getCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getMessage(), is(ErrorCode.CLIENT_NOT_FOUND.getDescription()));
        }
    }
}