package org.rootservices.authorization.grant.code.protocol.authorization;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestScopesRepository;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.security.RandomString;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/23/15.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantAuthCodeImplTest {
    @Mock
    private RandomString mockRandomString;
    @Mock
    private MakeAuthCode mockMakeAuthCode;
    @Mock
    private AuthCodeRepository mockAuthCodeRepository;
    @Mock
    private AccessRequestRepository mockAccessRequestRepository;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private AccessRequestScopesRepository mockAccessRequestScopesRepository;

    private GrantAuthCode subject;

    @Before
    public void setUp() {
        subject = new GrantAuthCodeImpl(
            mockRandomString, mockMakeAuthCode, mockAuthCodeRepository, mockAccessRequestRepository, mockScopeRepository, mockAccessRequestScopesRepository
        );
    }

    @Test
    public void testRun() throws Exception {
        // parameters to pass into method in test
        UUID resourceOwnerUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.of(new URI("https://rootservices.org"));
        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("profile");

        // responses from mocks/spy objects.
        String randomString = "randomString";
        AuthCode authCode = new AuthCode();
        authCode.setUuid(UUID.randomUUID());

        List<Scope> scopes = new ArrayList<>();
        Scope scope = new Scope(UUID.randomUUID(), "profile");
        scopes.add(scope);

        when(mockRandomString.run()).thenReturn(randomString);
        when(mockMakeAuthCode.run(
                resourceOwnerUUID, clientUUID, randomString, subject.getSecondsToExpiration()
        )).thenReturn(authCode);

        when(mockScopeRepository.findByName(scopeNames)).thenReturn(scopes);

        ArgumentCaptor<AccessRequest> ARCaptor = ArgumentCaptor.forClass(AccessRequest.class);
        ArgumentCaptor<AccessRequestScope> ARSCaptor = ArgumentCaptor.forClass(AccessRequestScope.class);

        String actual = subject.run(resourceOwnerUUID, clientUUID, redirectURI, scopeNames);

        assertThat(actual).isEqualTo(randomString);
        verify(mockAuthCodeRepository).insert(authCode);
        verify(mockAccessRequestRepository).insert(ARCaptor.capture());
        verify(mockAccessRequestScopesRepository).insert(ARSCaptor.capture());

        // check access request assigned correct values.
        assertThat(ARCaptor.getValue().getUuid()).isNotNull();
        assertThat(ARCaptor.getValue().getRedirectURI()).isEqualTo(redirectURI);
        assertThat(ARCaptor.getValue().getAuthCodeUUID()).isEqualTo(authCode.getUuid());

        // check that access request scope assigned correct values.
        assertThat(ARSCaptor.getValue().getUuid()).isNotNull();
        assertThat(ARSCaptor.getValue().getAccessRequestUUID()).isEqualTo(ARCaptor.getValue().getUuid());
        assertThat(ARSCaptor.getValue().getScopeUUID()).isEqualTo(scope.getUuid());

    }
}