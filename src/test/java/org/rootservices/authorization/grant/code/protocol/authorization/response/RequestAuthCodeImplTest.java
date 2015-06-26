package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.ValidateParams;
import org.rootservices.authorization.grant.code.protocol.authorization.response.*;
import org.rootservices.authorization.grant.code.protocol.authorization.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/20/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestAuthCodeImplTest {

    @Mock
    private ValidateParams mockValidateParams;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private GrantAuthCode mockGrantAuthCode;
    @Mock
    private MakeAuthResponse mockMakeAuthResponse;

    private RequestAuthCode subject;

    @Before
    public void setUp() {
        subject = new RequestAuthCodeImpl(
                mockValidateParams,
                mockLoginResourceOwner,
                mockGrantAuthCode,
                mockMakeAuthResponse
        );
    }

    public AuthCodeInput makeAuthCodeInput(UUID clientId, ResponseType rt, String scope) {
        AuthCodeInput input = new AuthCodeInput();
        input.setUserName("resourceOwner@rootservices.org");
        input.setPlainTextPassword("plainTextPassword");

        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());
        input.setClientIds(clientIds);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(rt.toString());
        input.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add(scope.toString());
        input.setScopes(scopes);

        return input;
    }

    public AuthRequest makeAuthRequest(String scope) throws URISyntaxException {
        List<String> scopes = new ArrayList<>();
        scopes.add(scope);

        AuthRequest authRequest = new AuthRequest(
                UUID.randomUUID(),
                ResponseType.CODE,
                Optional.of(new URI("https://rootservices.org")),
                scopes
        );

        return authRequest;
    }

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        String scope = "profile";

        // parameter to pass into method in test
        AuthCodeInput input = makeAuthCodeInput(clientId, rt, scope);

        // responses from mocked dependencies.
        AuthRequest authRequest = makeAuthRequest(scope);
        UUID resourceOwnerUUID = UUID.randomUUID();
        String randomString = "randomString";

        // expected response from method in test
        AuthResponse expectedAuthResponse = new AuthResponse();
        expectedAuthResponse.setCode(randomString);
        expectedAuthResponse.setState(authRequest.getState());
        expectedAuthResponse.setRedirectUri(authRequest.getRedirectURI().get());

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
        ).thenReturn(resourceOwnerUUID);

        when(mockGrantAuthCode.run(
                        resourceOwnerUUID,
                        authRequest.getClientId(),
                        authRequest.getRedirectURI(),
                        authRequest.getScopes())
        ).thenReturn(randomString);

        when(mockMakeAuthResponse.run(
                authRequest.getClientId(),
                randomString,
                authRequest.getState(),
                authRequest.getRedirectURI()
        )).thenReturn(expectedAuthResponse);

        AuthResponse actual = subject.run(input);

        assertThat(actual.getCode()).isEqualTo(expectedAuthResponse.getCode());
        assertThat(actual.getRedirectUri()).isEqualTo(expectedAuthResponse.getRedirectUri());
        assertThat(actual.getState()).isEqualTo(expectedAuthResponse.getState());
    }

    @Test
    public void testRunFailsLogin() throws URISyntaxException, UnauthorizedException {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        String scope = "profile";

        // parameters to method in test
        AuthCodeInput input = makeAuthCodeInput(clientId, rt, scope);

        // stubbing
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
        }

        assertThat(authResponse).isNull();
        assertThat(expectedException).isNotNull();
    }
}