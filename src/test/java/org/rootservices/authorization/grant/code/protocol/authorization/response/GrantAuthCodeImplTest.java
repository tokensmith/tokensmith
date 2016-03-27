package org.rootservices.authorization.grant.code.protocol.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestScopesRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/23/15.
 *
 */
public class GrantAuthCodeImplTest {
    @Mock
    private AccessRequestRepository mockAccessRequestRepository;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private AccessRequestScopesRepository mockAccessRequestScopesRepository;
    @Mock
    private InsertAuthCodeWithRetry mockInsertAuthCodeWithRetry;

    private GrantAuthCode subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new GrantAuthCodeImpl(
            mockAccessRequestRepository, mockScopeRepository, mockAccessRequestScopesRepository, mockInsertAuthCodeWithRetry
        );
    }

    // TODO: this test is too complex come back and fix it.
    @Test
    public void testRun() throws Exception {
        // parameters to pass into run method.
        UUID resourceOwnerId = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.of(new URI("https://rootservices.org"));

        // scopes to add to the access request.
        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("profile");

        List<Scope> scopes = new ArrayList<>();
        Scope scope = new Scope(UUID.randomUUID(), "profile");
        scopes.add(scope);

        when(mockScopeRepository.findByName(scopeNames)).thenReturn(scopes);

        ArgumentCaptor<AccessRequest> ARCaptor = ArgumentCaptor.forClass(AccessRequest.class);
        ArgumentCaptor<AccessRequestScope> ARSCaptor = ArgumentCaptor.forClass(AccessRequestScope.class);

        when(mockInsertAuthCodeWithRetry.run(any(AccessRequest.class), anyInt())).thenReturn("randomString");

        String actual = subject.run(resourceOwnerId, clientUUID, redirectURI, scopeNames);

        assertThat(actual).isEqualTo("randomString");

        verify(mockAccessRequestRepository).insert(ARCaptor.capture());
        verify(mockAccessRequestScopesRepository).insert(ARSCaptor.capture());

        // check access request assigned correct values.
        assertThat(ARCaptor.getValue().getUuid()).isNotNull();
        assertThat(ARCaptor.getValue().getRedirectURI()).isEqualTo(redirectURI);
        assertThat(ARCaptor.getValue().getClientUUID()).isEqualTo(clientUUID);

        // check that access request scope assigned correct values.
        assertThat(ARSCaptor.getValue().getUuid()).isNotNull();
        assertThat(ARSCaptor.getValue().getAccessRequestUUID()).isEqualTo(ARCaptor.getValue().getUuid());
        assertThat(ARSCaptor.getValue().getScope().getUuid()).isEqualTo(scope.getUuid());

    }
}