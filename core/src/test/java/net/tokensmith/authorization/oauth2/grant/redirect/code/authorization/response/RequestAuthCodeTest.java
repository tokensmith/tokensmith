package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request.ValidateCodeGrant;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.request.context.GetConfidentialClientRedirectUri;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import net.tokensmith.repository.entity.ResourceOwner;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/20/15.
 */
public class RequestAuthCodeTest {

    @Mock
    private ValidateCodeGrant mockValidateCodeGrant;
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
                mockValidateCodeGrant,
                mockLoginResourceOwner,
                mockIssueAuthCode,
                mockAuthResponseFactory,
                mockGetConfidentialClientRedirectUri
        );
    }


    @Test
    public void testRun() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        UUID clientId = UUID.randomUUID();

        // response from mockValidateParams.
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "CODE");

        // response from mockLoginResourceOwner.
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        // response from mockGrantAuthCode.
        String randomString = "randomString";

        // expected response from method in test
        AuthResponse expectedAuthResponse = new AuthResponse();
        expectedAuthResponse.setCode(randomString);
        expectedAuthResponse.setState(authRequest.getState());
        expectedAuthResponse.setRedirectUri(new URI(FixtureFactory.SECURE_REDIRECT_URI));

        Map<String, List<String>> params = FixtureFactory.makeOAuthParameters(clientId, "CODE");

        // stubbing
        when(mockValidateCodeGrant.run(params)).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);

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

        AuthResponse actual = subject.run(userName, password, params);

        assertThat(actual.getCode(), is(expectedAuthResponse.getCode()));
        assertThat(actual.getRedirectUri(), is(expectedAuthResponse.getRedirectUri()));
        assertThat(actual.getState(), is(expectedAuthResponse.getState()));
    }

    @Test
    public void runWhenFailsLoginShouldThrowUnauthorizedException() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        UUID clientId = UUID.randomUUID();

        Map<String, List<String>> params = FixtureFactory.makeOAuthParameters(clientId, "CODE");

        // response from mockValidateParams
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "CODE");

        when(mockValidateCodeGrant.run(params)).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(userName, password)).thenThrow(UnauthorizedException.class);

        AuthResponse authResponse = null;
        UnauthorizedException actual = null;
        try {
            authResponse = subject.run(userName, password, params);
        } catch (UnauthorizedException e) {
            verify(mockIssueAuthCode, never()).run(
                any(UUID.class), any(UUID.class), FixtureFactory.anyOptionalURI(), anyList()
            );
            actual = e;
        }

        assertThat(authResponse, is(nullValue()));
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void runWhenAuthCodeInsertExceptionShouldThrowInformClientException() throws Exception {
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        UUID clientId = UUID.randomUUID();

        // response from mockValidateParams
        AuthRequest authRequest = FixtureFactory.makeAuthRequest(clientId, "CODE");

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        Map<String, List<String>> params = FixtureFactory.makeOAuthParameters(clientId, "CODE");
        when(mockValidateCodeGrant.run(params)).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);

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
            subject.run(userName, password, params);
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
        assertThat(actual.getState().get(), is("some-state"));
        assertThat(actual.getCause(), is(aci));
    }
}