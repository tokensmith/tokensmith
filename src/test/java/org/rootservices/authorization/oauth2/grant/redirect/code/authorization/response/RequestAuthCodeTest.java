package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResourceOwner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/20/15.
 */
public class RequestAuthCodeTest {

    @Mock
    private ValidateParams mockValidateParams;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private IssueAuthCode mockIssueAuthCode;
    @Mock
    private AuthResponseFactory mockAuthResponseFactory;
    @Mock
    private GetConfidentialClientRedirectUri mockGetConfidentialClientRedirectUri;

    private RequestAuthCode subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestAuthCode(
                mockValidateParams,
                mockLoginResourceOwner,
                mockIssueAuthCode,
                mockAuthResponseFactory,
                mockGetConfidentialClientRedirectUri
        );
    }

    public AuthRequest makeAuthRequest(InputParams input) throws URISyntaxException {

        Optional<URI> redirectUri = Optional.empty();
        if (input.getRedirectUris() != null && input.getRedirectUris().get(0) != null ) {
            redirectUri = Optional.of(new URI(input.getRedirectUris().get(0)));
        }

        Optional<String> state = Optional.empty();
        if (input.getStates() != null && input.getStates().size() > 0 && input.getStates().get(0) != null) {
            state = Optional.of(input.getStates().get(0));
        }

        AuthRequest authRequest = new AuthRequest(
                UUID.fromString(input.getClientIds().get(0)),
                input.getResponseTypes(),
                redirectUri,
                input.getScopes(),
                state
        );

        return authRequest;
    }

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        String scope = "profile";

        // parameter to pass into method in test
        InputParams input = FixtureFactory.makeInputParams(clientId, "CODE", scope);

        // response from mockValidateParams.
        AuthRequest authRequest = makeAuthRequest(input);

        // response from mockLoginResourceOwner.
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        // response from mockGrantAuthCode.
        String randomString = "randomString";

        // expected response from method in test
        AuthResponse expectedAuthResponse = new AuthResponse();
        expectedAuthResponse.setCode(randomString);
        expectedAuthResponse.setState(authRequest.getState());
        expectedAuthResponse.setRedirectUri(new URI(FixtureFactory.SECURE_REDIRECT_URI));

        // stubbing
        when(mockValidateParams.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(
                        input.getUserName(),
                        input.getPlainTextPassword())
        ).thenReturn(resourceOwner);

        when(mockIssueAuthCode.run(
                        resourceOwner.getId(),
                        authRequest.getClientId(),
                        authRequest.getRedirectURI(),
                        authRequest.getScopes())
        ).thenReturn(randomString);

        when(mockAuthResponseFactory.makeAuthResponse(
                authRequest.getClientId(),
                randomString,
                authRequest.getState(),
                authRequest.getRedirectURI()
        )).thenReturn(expectedAuthResponse);

        AuthResponse actual = subject.run(input);

        assertThat(actual.getCode(), is(expectedAuthResponse.getCode()));
        assertThat(actual.getRedirectUri(), is(expectedAuthResponse.getRedirectUri()));
        assertThat(actual.getState(), is(expectedAuthResponse.getState()));
    }

    @Test
    public void runWhenFailsLoginShouldThrowUnauthorizedException() throws URISyntaxException, UnauthorizedException, InformClientException, InformResourceOwnerException, AuthCodeInsertException {
        UUID clientId = UUID.randomUUID();
        String scope = "profile";

        // parameters to method in test
        InputParams input = FixtureFactory.makeInputParams(clientId, "CODE", scope);

        // response from mockValidateParams
        AuthRequest authRequest = makeAuthRequest(input);

        when(mockValidateParams.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(
                input.getUserName(),
                input.getPlainTextPassword()
        )).thenThrow(UnauthorizedException.class);

        AuthResponse authResponse = null;
        UnauthorizedException actual = null;
        try {
            authResponse = subject.run(input);
        } catch (UnauthorizedException e) {
            verify(mockIssueAuthCode, never()).run(
                any(UUID.class), any(UUID.class), any(Optional.class), anyListOf(String.class)
            );
            actual = e;
        }

        assertThat(authResponse, is(nullValue()));
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void runWhenAuthCodeInsertExceptionShouldThrowInformClientException() throws URISyntaxException, UnauthorizedException, InformClientException, InformResourceOwnerException, AuthCodeInsertException {
        UUID clientId = UUID.randomUUID();
        String scope = "profile";

        // parameters to method in test
        InputParams input = FixtureFactory.makeInputParams(clientId, "CODE", scope);
        input.getStates().add("state");

        // response from mockValidateParams
        AuthRequest authRequest = makeAuthRequest(input);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        when(mockValidateParams.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(
                input.getUserName(),
                input.getPlainTextPassword()
        )).thenReturn(resourceOwner);

        AuthCodeInsertException aci = new AuthCodeInsertException("test", null);
        when(mockIssueAuthCode.run(
                resourceOwner.getId(),
                clientId,
                authRequest.getRedirectURI(),
                authRequest.getScopes()))
        .thenThrow(aci);

        when(mockGetConfidentialClientRedirectUri.run(
                clientId,
                authRequest.getRedirectURI(),
                aci)
        ).thenReturn(FixtureFactory.makeSecureRedirectUri());

        InformClientException actual = null;
        try {
            subject.run(input);
        } catch (InformClientException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Failed to issue authorization code"));
        assertThat(actual.getError(), is("server_error"));
        assertThat(actual.getDescription(), is(ErrorCode.SERVER_ERROR.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.SERVER_ERROR.getCode()));
        assertThat(actual.getRedirectURI(), is(FixtureFactory.makeSecureRedirectUri()));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("state"));
        assertThat(actual.getCause(), is(aci));
    }
}