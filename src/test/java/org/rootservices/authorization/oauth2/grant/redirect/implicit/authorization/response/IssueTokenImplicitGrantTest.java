package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.InsertTokenGraphImplicitGrant;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
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
    @Mock
    private ClientTokenRepository mockClientTokenRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new IssueTokenImplicitGrant(mockInsertTokenGraphImplicitGrant, mockScopeRepository, mockResourceOwnerTokenRepository, mockClientTokenRepository);
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

        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph(clientId);
        when(mockInsertTokenGraphImplicitGrant.insertTokenGraph(scopes)).thenReturn(tokenGraph);

        TokenGraph actual = subject.run(clientId, resourceOwner, scopeNames);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(tokenGraph));

        verify(mockResourceOwnerTokenRepository, times(1)).insert(resourceOwnerTokenCaptor.capture());
        ResourceOwnerToken actualRot = resourceOwnerTokenCaptor.getValue();
        assertThat(actualRot.getId(), is(notNullValue()));
        assertThat(actualRot.getToken(), is(tokenGraph.getToken()));
        assertThat(actualRot.getResourceOwner(), is(resourceOwner));

        ArgumentCaptor<ClientToken> clientTokenArgumentCaptor = ArgumentCaptor.forClass(ClientToken.class);
        verify(mockClientTokenRepository, times(1)).insert(clientTokenArgumentCaptor.capture());
        ClientToken actualCt = clientTokenArgumentCaptor.getValue();
        assertThat(actualCt.getId(), is(notNullValue()));
        assertThat(actualCt.getTokenId(), is(tokenGraph.getToken().getId()));
        assertThat(actualCt.getClientId(), is(clientId));

    }
}