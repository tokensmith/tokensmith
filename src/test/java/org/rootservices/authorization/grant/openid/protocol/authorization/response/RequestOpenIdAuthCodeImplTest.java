package org.rootservices.authorization.grant.openid.protocol.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.grant.code.protocol.authorization.response.AuthCodeInput;
import org.rootservices.authorization.grant.code.protocol.authorization.response.AuthResponse;
import org.rootservices.authorization.grant.code.protocol.authorization.response.GrantAuthCode;
import org.rootservices.authorization.grant.code.protocol.authorization.response.RequestAuthCode;
import org.rootservices.authorization.grant.code.protocol.authorization.response.builder.AuthResponseBuilder;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.ValidateOpenIdParams;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.ResponseType;

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
public class RequestOpenIdAuthCodeImplTest {

    @Mock
    private ValidateOpenIdParams mockValidateOpenIdParams;
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
        subject = new RequestOpenIdAuthCodeImpl(
                mockValidateOpenIdParams,
                mockLoginResourceOwner,
                mockGrantAuthCode,
                mockAuthResponseBuilder
        );
    }

    public OpenIdAuthRequest makeOpenIdAuthRequest(AuthCodeInput input) throws URISyntaxException {

        UUID clientId = UUID.fromString(input.getClientIds().get(0));
        ResponseType responseType = ResponseType.valueOf(input.getResponseTypes().get(0));
        URI redirectUri = new URI(input.getRedirectUris().get(0));

        Optional<String> state = Optional.empty();
        if (input.getStates() != null && input.getStates().get(0) != null) {
            state = Optional.of(input.getStates().get(0));
        }

        OpenIdAuthRequest authRequest = new OpenIdAuthRequest(
                clientId,
                responseType,
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
        List<String> redirectUris = new ArrayList();
        redirectUris.add(FixtureFactory.SECURE_REDIRECT_URI);
        input.setRedirectUris(redirectUris);

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

        when(mockValidateOpenIdParams.run(
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
                        Optional.of(authRequest.getRedirectURI()),
                        authRequest.getScopes())
        ).thenReturn(randomString);

        when(mockAuthResponseBuilder.run(
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