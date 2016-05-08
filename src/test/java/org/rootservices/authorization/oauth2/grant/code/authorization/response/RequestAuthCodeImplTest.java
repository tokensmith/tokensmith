package org.rootservices.authorization.oauth2.grant.code.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.*;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.builder.AuthResponseBuilder;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/20/15.
 */
public class RequestAuthCodeImplTest {

    @Mock
    private ValidateParams mockValidateParams;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private GrantAuthCode mockGrantAuthCode;
    @Mock
    private AuthResponseBuilder mockAuthResponseBuilder;

    private RequestAuthCode subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestAuthCodeImpl(
                mockValidateParams,
                mockLoginResourceOwner,
                mockGrantAuthCode,
                mockAuthResponseBuilder
        );
    }

    public AuthRequest makeAuthRequest(AuthCodeInput input) throws URISyntaxException {

        Optional<URI> redirectUri = Optional.empty();
        if (input.getRedirectUris() != null && input.getRedirectUris().get(0) != null ) {
            redirectUri = Optional.of(new URI(input.getRedirectUris().get(0)));
        }

        Optional<String> state = Optional.empty();
        if (input.getStates() != null && input.getStates().get(0) != null) {
            state = Optional.of(input.getStates().get(0));
        }

        AuthRequest authRequest = new AuthRequest(
                UUID.fromString(input.getClientIds().get(0)),
                ResponseType.valueOf(input.getResponseTypes().get(0)),
                redirectUri,
                input.getScopes(),
                state
        );

        return authRequest;
    }

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        String scope = "profile";

        // parameter to pass into method in test
        AuthCodeInput input = FixtureFactory.makeAuthCodeInput(clientId, rt, scope);

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

        when(mockGrantAuthCode.run(
                        resourceOwner.getUuid(),
                        authRequest.getClientId(),
                        authRequest.getRedirectURI(),
                        authRequest.getScopes())
        ).thenReturn(randomString);

        when(mockAuthResponseBuilder.run(
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
    public void failsLoginShouldThrowUnauthorizedException() throws URISyntaxException, UnauthorizedException, InformClientException, InformResourceOwnerException, AuthCodeInsertException {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        String scope = "profile";

        // parameters to method in test
        AuthCodeInput input = FixtureFactory.makeAuthCodeInput(clientId, rt, scope);

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
        UnauthorizedException expectedException = null;
        try {
            authResponse = subject.run(input);
        } catch (UnauthorizedException e) {
            verify(mockGrantAuthCode, never()).run(
                any(UUID.class), any(UUID.class), any(Optional.class), anyListOf(String.class)
            );
            expectedException = e;
        } catch (InformResourceOwnerException e) {
            fail("Expected UnauthorizedException");
        } catch (InformClientException e) {
            fail("Expected UnauthorizedException");
        } catch (AuthCodeInsertException e) {
            fail("Expected UnauthorizedException");
        }

        assertThat(authResponse, is(nullValue()));
        assertThat(expectedException, is(notNullValue()));
    }
}