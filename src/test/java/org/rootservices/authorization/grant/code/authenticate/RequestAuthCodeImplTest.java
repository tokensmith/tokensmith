package org.rootservices.authorization.grant.code.authenticate;

import helper.ValidateParamsAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.ValidateParams;
import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.authenticate.input.AuthCodeInput;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.grant.code.request.ValidateAuthRequest;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.security.RandomString;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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

    private RequestAuthCode subject;

    @Before
    public void setUp() {
        subject = new RequestAuthCodeImpl(
                mockValidateParams,
                mockLoginResourceOwner,
                mockGrantAuthCode
        );
    }

    public AuthCodeInput makeAuthCodeInput(UUID clientId, ResponseType rt, Scope scope) {
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

    @Test
    public void testRun() throws Exception {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        Scope scope = Scope.PROFILE;

        // parameters to method in test
        AuthCodeInput input = makeAuthCodeInput(clientId, rt, scope);

        // responses from mocks/spy objects.
        List<Scope> scopes = new ArrayList<>();
        scopes.add(scope);

        AuthRequest authRequest = new AuthRequest(
                UUID.randomUUID(),
                ResponseType.CODE,
                Optional.of(new URI("https://rootservices.org")),
                scopes
        );

        UUID resourceOwnerUUID = UUID.randomUUID();
        String randomString = "randomString";

        // spy
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
                authRequest.getRedirectURI())
        ).thenReturn(randomString);

        String actual = subject.run(input);

        assertThat(actual).isEqualTo(randomString);
    }

    @Test
    public void testRunFailsLogin() throws URISyntaxException, UnauthorizedException {
        UUID clientId = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        Scope scope = Scope.PROFILE;

        // parameters to method in test
        AuthCodeInput input = makeAuthCodeInput(clientId, rt, scope);

        // responses from mocks/spy objects.
        List<Scope> scopes = new ArrayList<>();
        scopes.add(scope);

        AuthRequest authRequest = new AuthRequest(
                UUID.randomUUID(),
                ResponseType.CODE,
                Optional.of(new URI("https://rootservices.org")),
                scopes
        );

        // spy
        when(mockLoginResourceOwner.run(
                input.getUserName(),
                input.getPlainTextPassword()
        )).thenThrow(UnauthorizedException.class);

        String authorizationCode = null;
        UnauthorizedException expectedException = null;
        try {
            authorizationCode = subject.run(input);
        } catch (UnauthorizedException e) {
            verify(mockGrantAuthCode, never()).run(any(UUID.class), any(UUID.class), any(Optional.class));
            expectedException = e;
        } catch (InformResourceOwnerException e) {
            fail("Expected UnauthorizedException");
        } catch (InformClientException e) {
            fail("Expected UnauthorizedException");
        }

        assertThat(authorizationCode).isNull();
        assertThat(expectedException).isNotNull();
    }
}