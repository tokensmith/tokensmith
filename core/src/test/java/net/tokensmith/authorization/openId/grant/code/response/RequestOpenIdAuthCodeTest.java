package net.tokensmith.authorization.openId.grant.code.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.IssueAuthCode;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.response.RequestOpenIdAuthCode;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;

import java.net.URI;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


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
            authRequest.getScopes(),
            authRequest.getNonce())
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

    @Test
    public void runWhenFailsToIssueAuthCodeShouldThrowInformClientException() throws Exception {
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

        DuplicateRecordException dre = new DuplicateRecordException();
        AuthCodeInsertException cause = new AuthCodeInsertException("test", dre);

        when(mockIssueAuthCode.run(
            resourceOwner.getId(),
            authRequest.getClientId(),
            Optional.of(authRequest.getRedirectURI()),
            authRequest.getScopes(),
            authRequest.getNonce())
        ).thenThrow(cause);

        InformClientException actual = null;
        try {
            subject.run(userName, password, params);
        } catch (InformClientException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("failed to issue authorization code"));
        assertThat(actual.getError(), is("server_error"));
        assertThat(actual.getDescription(), is("failed to issue authorization code"));
        assertThat(actual.getRedirectURI(), is(authRequest.getRedirectURI()));
        assertThat(actual.getState(), is(authRequest.getState()));
        assertThat(actual.getCause(), is(cause));
    }
}