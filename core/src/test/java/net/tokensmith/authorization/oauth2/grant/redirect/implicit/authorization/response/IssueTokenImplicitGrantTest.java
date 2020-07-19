package net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization.InsertTokenGraphImplicitGrant;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.repo.ResourceOwnerTokenRepository;
import net.tokensmith.repository.repo.ScopeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 6/24/16.
 */
public class IssueTokenImplicitGrantTest {
    private IssueTokenImplicitGrant subject;

    @Mock
    private InsertTokenGraphImplicitGrant mockInsertTokenGraphImplicitGrant;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private ResourceOwnerTokenRepository mockResourceOwnerTokenRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenImplicitGrant(mockInsertTokenGraphImplicitGrant, mockScopeRepository, mockResourceOwnerTokenRepository);
    }

    @Test
    public void grantShouldReturnToken() throws Exception{
        UUID clientId = UUID.randomUUID();
        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("profile");

        ArgumentCaptor<ResourceOwnerToken> resourceOwnerTokenCaptor = ArgumentCaptor.forClass(ResourceOwnerToken.class);

        List<Scope> scopes = FixtureFactory.makeScopes();
        when(mockScopeRepository.findByNames(scopeNames)).thenReturn(scopes);

        List<Client> audience = FixtureFactory.makeAudience(clientId);
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph(clientId, audience);
        when(mockInsertTokenGraphImplicitGrant.insertTokenGraph(clientId, scopes, audience, Optional.empty())).thenReturn(tokenGraph);

        TokenGraph actual = subject.run(clientId, resourceOwner, scopeNames, audience, Optional.empty());

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(tokenGraph));

        verify(mockResourceOwnerTokenRepository, times(1)).insert(resourceOwnerTokenCaptor.capture());
        ResourceOwnerToken actualRot = resourceOwnerTokenCaptor.getValue();
        assertThat(actualRot.getId(), is(notNullValue()));
        assertThat(actualRot.getToken(), is(tokenGraph.getToken()));
        assertThat(actualRot.getResourceOwner(), is(resourceOwner));
    }
}