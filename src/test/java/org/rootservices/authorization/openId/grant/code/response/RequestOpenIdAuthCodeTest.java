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
import java.util.*;

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

    @Test
    public void testRun() throws Exception {

        // parameter to pass into method in test
        UUID clientId = UUID.randomUUID();
        Map<String, List<String>> params = FixtureFactory.makeOpenIdParameters(clientId, "CODE");
        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;

        // response from mockValidateParams.
        OpenIdAuthRequest authRequest = FixtureFactory.makeOpenIdAuthRequest(clientId, "CODE");

        // response from mockLoginResourceOwner.
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();

        // response from mockGrantAuthCode.
        String randomString = "randomString";

        // expected response from method in test
        AuthResponse expectedAuthResponse = new AuthResponse();
        expectedAuthResponse.setCode(randomString);
        expectedAuthResponse.setState(authRequest.getState());
        expectedAuthResponse.setRedirectUri(new URI(FixtureFactory.SECURE_REDIRECT_URI));

        when(mockValidateOpenIdCodeResponseType.run(params)).thenReturn(authRequest);
        when(mockLoginResourceOwner.run(userName, password)).thenReturn(resourceOwner);

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

        AuthResponse actual = subject.run(userName, password, params);

        assertThat(actual.getCode(), is(expectedAuthResponse.getCode()));
        assertThat(actual.getRedirectUri(), is(expectedAuthResponse.getRedirectUri()));
        assertThat(actual.getState(), is(expectedAuthResponse.getState()));
    }

}