package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response;

import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.entity.AccessRequestScope;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.repo.AccessRequestRepository;
import net.tokensmith.repository.repo.AccessRequestScopesRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 4/23/15.
 *
 */
public class IssueAuthCodeTest {
    @Mock
    private AccessRequestRepository mockAccessRequestRepository;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private AccessRequestScopesRepository mockAccessRequestScopesRepository;
    @Mock
    private InsertAuthCodeWithRetry mockInsertAuthCodeWithRetry;

    private IssueAuthCode subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueAuthCode(
            mockAccessRequestRepository, mockScopeRepository, mockAccessRequestScopesRepository, mockInsertAuthCodeWithRetry
        );
    }

    public List<String> scopeNames() {
        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("profile");

        return scopeNames;
    }

    public List<Scope> scopes(List<String> scopeNames) {
        List<Scope> scopes = new ArrayList<>();
        for(String scopeName: scopeNames) {
            Scope scope = new Scope(UUID.randomUUID(), scopeName);
            scopes.add(scope);
        }

        return scopes;
    }

    @Test
    public void testRunWhenEmptyNonce() throws Exception {
        // parameters to pass into run method.
        UUID resourceOwnerId = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        Optional<URI> redirectURI = Optional.of(new URI("https://tokensmith.net"));


        List<String> scopeNames = scopeNames();
        List<Scope> scopes = scopes(scopeNames);
        when(mockScopeRepository.findByNames(scopeNames)).thenReturn(scopes);

        ArgumentCaptor<AccessRequest> ARCaptor = ArgumentCaptor.forClass(AccessRequest.class);
        ArgumentCaptor<AccessRequestScope> ARSCaptor = ArgumentCaptor.forClass(AccessRequestScope.class);

        when(mockInsertAuthCodeWithRetry.run(any(AccessRequest.class))).thenReturn("auth-code");

        String actual = subject.run(resourceOwnerId, clientUUID, redirectURI, scopeNames, Optional.empty());

        assertThat(actual, is("auth-code"));

        verify(mockAccessRequestRepository).insert(ARCaptor.capture());
        verify(mockAccessRequestScopesRepository).insert(ARSCaptor.capture());

        // check access request assigned correct values.
        assertThat(ARCaptor.getValue().getId(), is(notNullValue()));
        assertThat(ARCaptor.getValue().getRedirectURI(), is(redirectURI));
        assertThat(ARCaptor.getValue().getClientId(), is(clientUUID));
        assertThat(ARCaptor.getValue().getNonce().isPresent(), is(false));

        // check that access request scope assigned correct values.
        assertThat(ARSCaptor.getValue().getId(), is(notNullValue()));
        assertThat(ARSCaptor.getValue().getAccessRequestId(), is(ARCaptor.getValue().getId()));
        assertThat(ARSCaptor.getValue().getScope().getId(), is(scopes.get(0).getId()));

    }
}