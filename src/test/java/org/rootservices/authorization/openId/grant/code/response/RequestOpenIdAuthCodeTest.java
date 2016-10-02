package org.rootservices.authorization.openId.grant.code.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.IssueAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.response.RequestOpenIdAuthCode;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.persistence.entity.ResourceOwner;

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
 * Created by tommackenzie on 10/27/15.
 */
public class RequestOpenIdAuthCodeTest {

    @Mock
    private ValidateOpenIdCodeResponseType mockValidateOpenIdCodeResponseType;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private IssueAuthCode mockIssueAuthCode;
    @Mock
    private AuthResponseFactory mockAuthResponseFactory;

    private RequestOpenIdAuthCode subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestOpenIdAuthCode(
                mockValidateOpenIdCodeResponseType,
                mockLoginResourceOwner,
                mockIssueAuthCode,
                mockAuthResponseFactory
        );
    }

    public OpenIdAuthRequest makeOpenIdAuthRequest(OpenIdInputParams input) throws URISyntaxException {

        UUID clientId = UUID.fromString(input.getClientIds().get(0));
        URI redirectUri = new URI(input.getRedirectUris().get(0));

        Optional<String> state = Optional.empty();
        if (input.getStates() != null && input.getStates().get(0) != null) {
            state = Optional.of(input.getStates().get(0));
        }

        OpenIdAuthRequest authRequest = new OpenIdAuthRequest(
                clientId,
                input.getResponseTypes(),
                redirectUri,
                input.getScopes(),
                state
        );

        return authRequest;
    }

    @Test
    public void testRun() throws Exception {

        // parameter to pass into method in test
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams("CODE");

        // response from mockValidateParams.
        OpenIdAuthRequest authRequest = makeOpenIdAuthRequest(input);

        // response from mockLoginResourceOwner.
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        // response from mockGrantAuthCode.
        String randomString = "randomString";

        // expected response from method in test
        AuthResponse expectedAuthResponse = new AuthResponse();
        expectedAuthResponse.setCode(randomString);
        expectedAuthResponse.setState(authRequest.getState());
        expectedAuthResponse.setRedirectUri(new URI(FixtureFactory.SECURE_REDIRECT_URI));

        when(mockValidateOpenIdCodeResponseType.run(
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
                        Optional.of(authRequest.getRedirectURI()),
                        authRequest.getScopes())
        ).thenReturn(randomString);

        when(mockAuthResponseFactory.makeAuthResponse(
                authRequest.getClientId(),
                randomString,
                authRequest.getState(),
                Optional.of(authRequest.getRedirectURI())
        )).thenReturn(expectedAuthResponse);

        AuthResponse actual = subject.run(input);

        assertThat(actual.getCode(), is(expectedAuthResponse.getCode()));
        assertThat(actual.getRedirectUri(), is(expectedAuthResponse.getRedirectUri()));
        assertThat(actual.getState(), is(expectedAuthResponse.getState()));
    }

}