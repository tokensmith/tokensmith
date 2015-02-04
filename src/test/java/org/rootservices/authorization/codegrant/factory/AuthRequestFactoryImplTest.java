package org.rootservices.authorization.codegrant.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.codegrant.factory.optional.RedirectUriFactory;
import org.rootservices.authorization.codegrant.factory.optional.ScopesFactory;
import org.rootservices.authorization.codegrant.factory.required.ClientIdFactory;
import org.rootservices.authorization.codegrant.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.context.GetClientRedirectURI;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthRequestFactoryImplTest {

    @Mock
    private ClientIdFactory mockClientIdFactory;

    @Mock
    private ResponseTypeFactory mockResponseTypeFactory;

    @Mock
    private RedirectUriFactory mockRedirectUriFactory;

    @Mock
    private ScopesFactory mockScopesFactory;

    @Mock
    private GetClientRedirectURI getClientRedirectURI;

    private AuthRequestFactory subject;

    @Before
    public void setUp() {
        subject = new AuthRequestFactoryImpl(mockClientIdFactory, mockResponseTypeFactory, mockRedirectUriFactory,mockScopesFactory, getClientRedirectURI);
    }

    @Test
    public void testMakeAuthRequest() throws Exception {
        List<String> clientIds = new ArrayList<>();
        UUID expectedClientId = UUID.randomUUID();
        clientIds.add(expectedClientId.toString());

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedClientId);

        List<String> responseTypes = new ArrayList<>();
        ResponseType expectedResponseType = ResponseType.CODE;
        responseTypes.add(expectedResponseType.toString());

        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenReturn(expectedResponseType);

        List<String> redirectUris = new ArrayList<>();
        Optional<URI> expectedRedirectUri = Optional.ofNullable(URI.create("https://rootservices.org"));
        redirectUris.add(expectedRedirectUri.toString());

        when(mockRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);

        List<Scope> expectedScopes = new ArrayList<>();
        expectedScopes.add(Scope.PROFILE);

        List<String> scopes = new ArrayList<>();
        scopes.add(expectedScopes.get(0).toString());

        when(mockScopesFactory.makeScopes(scopes)).thenReturn(expectedScopes);

        AuthRequest actual = subject.makeAuthRequest(clientIds, responseTypes, redirectUris, scopes);

        assertThat(actual.getClientId()).isEqualTo(expectedClientId);
        assertThat(actual.getResponseType()).isEqualTo(expectedResponseType);
        assertThat(actual.getRedirectURI()).isEqualTo(expectedRedirectUri);
        assertThat(actual.getScopes()).isEqualTo(expectedScopes);

    }
}