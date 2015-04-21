package org.rootservices.authorization.grant.code.authenticate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AuthRequestRepository;
import org.rootservices.authorization.security.RandomString;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
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
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeAuthCode mockMakeAuthCode;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
    @Mock
    private MakeAuthRequest mockMakeAuthRequest;
    @Mock
    private AuthRequestRepository mockAuthRequestRepository;

    private RequestAuthCode subject;

    @Before
    public void setUp() {
        subject = new RequestAuthCodeImpl(
                mockLoginResourceOwner,
                mockRandomString,
                mockMakeAuthCode,
                mockAuthCodeRepository,
                mockMakeAuthRequest,
                mockAuthRequestRepository
        );
    }

    @Test
    public void testRun() throws Exception {
        // parameters to method in test
        String userName = "resourceOwner@rootservices.org";
        String plainTextPassword = "plainTextPassword";

        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);
        AuthRequest authRequest = new AuthRequest(
                UUID.randomUUID(),
                ResponseType.CODE,
                Optional.of(new URI("https://rootservices.org")),
                scopes
        );

        // responses from mocks/spy objects.
        UUID resourceOwnerUUID = UUID.randomUUID();
        String randomString = "randomString";
        AuthCode authCode = new AuthCode();
        authCode.setUuid(UUID.randomUUID());
        org.rootservices.authorization.persistence.entity.AuthRequest authRequestEntity = new org.rootservices.authorization.persistence.entity.AuthRequest();

        when(mockLoginResourceOwner.run(userName, plainTextPassword)).thenReturn(resourceOwnerUUID);
        when(mockRandomString.run()).thenReturn(randomString);

        when(mockMakeAuthCode.run(
                resourceOwnerUUID, authRequest.getClientId(), randomString, subject.getSecondsToExpiration()
        )).thenReturn(authCode);

        when(mockMakeAuthRequest.run(authCode.getUuid(), authRequest)).thenReturn(authRequestEntity);

        subject.run(userName, plainTextPassword, authRequest);

        verify(mockAuthCodeRepository).insert(authCode);
        verify(mockAuthRequestRepository).insert(authRequestEntity);
    }

    @Test
    public void testRunFailsLogin() throws URISyntaxException, UnauthorizedException {
        String userName = "resourceOwner@rootservices.org";
        String plainTextPassword = "plainTextPassword";

        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);
        AuthRequest authRequest = new AuthRequest(
                UUID.randomUUID(),
                ResponseType.CODE,
                Optional.of(new URI("https://rootservices.org")),
                scopes
        );

        when(mockLoginResourceOwner.run(
                userName, plainTextPassword)
        ).thenThrow(UnauthorizedException.class);

        String authorizationCode = null;
        UnauthorizedException expectedException = null;
        try {
            authorizationCode = subject.run(userName, plainTextPassword, authRequest);
        } catch (UnauthorizedException e) {
            verify(mockAuthCodeRepository, never()).insert(any(AuthCode.class));
            verify(mockAuthRequestRepository, never()).insert(any(org.rootservices.authorization.persistence.entity.AuthRequest.class));
            expectedException = e;
        }

        assertThat(authorizationCode).isNull();
        assertThat(expectedException).isNotNull();
    }
}