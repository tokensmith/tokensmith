package org.rootservices.authorization.openId.grant.code.authorization.request.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.optional.ScopesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.optional.StateFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.CompareClientRedirectUri;
import org.rootservices.authorization.openId.grant.code.authorization.request.factory.required.OpenIdRedirectUriFactory;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.validator.exception.NoItemsError;
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
 * Created by tommackenzie on 10/2/15.
 */
public class OpenIdAuthRequestFactoryTest {

    @Mock
    private ClientIdFactory mockClientIdFactory;
    @Mock
    private OpenIdRedirectUriFactory mockOpenIdRedirectUriFactory;
    @Mock
    private ResponseTypeFactory mockResponseTypeFactory;
    @Mock
    private ScopesFactory mockScopesFactory;
    @Mock
    private StateFactory mockStateFactory;
    @Mock
    private CompareClientRedirectUri mockCompareClientRedirectUri;
    private OpenIdAuthRequestFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new OpenIdAuthRequestFactory(
                mockClientIdFactory,
                mockOpenIdRedirectUriFactory,
                mockResponseTypeFactory,
                mockScopesFactory,
                mockStateFactory,
                mockCompareClientRedirectUri
        );
    }

    public URI makeRedirectUri() throws URISyntaxException {
        return new URI("https://rootservices.org");
    }

    public ResponseType makeResponseType() {
        return ResponseType.CODE;
    }

    public List<String> makeScopes() {
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        return scopes;
    }

    public List<String> makeEmptyScopes() {
        List<String> scopes = new ArrayList<>();
        scopes.add("");
        return scopes;
    }

    public Optional<String> makeStates() {
        return Optional.of("state");
    }

    public Optional<String> makeEmptyStates() {
        return Optional.of("");
    }

    public List<String> buildList(Object o) {
        List<String> items = new ArrayList<>();
        items.add(o.toString());
        return items;
    }

    public List<String> buildListFromOptional(Optional<String> o) {
        List<String> items = new ArrayList<>();
        items.add(o.get().toString());
        return items;
    }

    @Test
    public void shouldBeOK() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException, ScopesException, StateException, InformClientException, InformResourceOwnerException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenReturn(expectedResponseType);
        when(mockScopesFactory.makeScopes(scopes)).thenReturn(scopes);
        when(mockStateFactory.makeState(states)).thenReturn(expectedStates);

        OpenIdAuthRequest actual = subject.make(
                clientIds, responseTypes, redirectUris, scopes, states
        );

        assertThat(actual.getClientId(), is(expectedUuid));
        assertThat(actual.getRedirectURI(), is(expectedRedirectUri));
        assertThat(actual.getResponseType(), is(expectedResponseType));
        assertThat(actual.getScopes(), is(scopes));
        assertThat(actual.getState(), is(expectedStates));
    }

    @Test
    public void invalidClientIdShouldThrowInformResourceOwnerException() throws ClientIdException, URISyntaxException {
        URI expectedRedirectUri = makeRedirectUri();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = new ArrayList<>();
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        NoItemsError cause = new NoItemsError("");
        ClientIdException exception = new ClientIdException(ErrorCode.CLIENT_ID_EMPTY_LIST, cause);
        when(mockClientIdFactory.makeClientId(clientIds)).thenThrow(exception);

        try {
            subject.make(clientIds, responseTypes, redirectUris, scopes, states);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(exception.getCode()));
            assertThat(e.getDomainCause(), is(exception));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void invalidRedirectUriShouldThrowInformResourceOwnerException() throws ClientIdException, URISyntaxException, RedirectUriException {
        UUID expectedUuid = UUID.randomUUID();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = new ArrayList<>();
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);

        NoItemsError cause = new NoItemsError("");
        RedirectUriException exception = new RedirectUriException(ErrorCode.REDIRECT_URI_EMPTY_LIST, cause);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenThrow(exception);

        try {
            subject.make(clientIds, responseTypes, redirectUris, scopes, states);
            fail("Expected InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            assertThat(e.getCode(), is(exception.getCode()));
            assertThat(e.getDomainCause(), is(exception));
        } catch (InformClientException e) {
            fail("Expected InformResourceOwnerException");
        }
    }

    @Test
    public void invalidResponseTypeShouldThrowInformClientException() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = new ArrayList<>();
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);

        NoItemsError cause = new NoItemsError("");
        ResponseTypeException exception = new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_LIST, cause);
        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenThrow(exception);

        try {
            subject.make(clientIds, responseTypes, redirectUris, scopes, states);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(exception.getCode()));
            assertThat(e.getDomainCause(), is(exception));
            assertThat(e.getRedirectURI(), is(expectedRedirectUri));
        }
    }

    @Test
    public void invalidScopesShouldThrowInformClientException() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException, ScopesException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeEmptyScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenReturn(expectedResponseType);

        EmptyValueError cause = new EmptyValueError("");
        ScopesException exception = new ScopesException(ErrorCode.SCOPES_EMPTY_VALUE, "invalid_scope", cause);
        when(mockScopesFactory.makeScopes(scopes)).thenThrow(exception);

        try {
            subject.make(clientIds, responseTypes, redirectUris, scopes, states);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(exception.getCode()));
            assertThat(e.getDomainCause(), is(exception));
            assertThat(e.getRedirectURI(), is(expectedRedirectUri));
        }
    }

    @Test
    public void invalidStatesShouldThrowInformClientException() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException, ScopesException, StateException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeEmptyStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenReturn(expectedResponseType);
        when(mockScopesFactory.makeScopes(scopes)).thenReturn(scopes);

        EmptyValueError cause = new EmptyValueError("");
        StateException exception = new StateException(ErrorCode.STATE_EMPTY_VALUE, cause);

        when(mockStateFactory.makeState(states)).thenThrow(exception);

        try {
            subject.make(clientIds, responseTypes, redirectUris, scopes, states);
            fail("Expected InformClientException");
        } catch (InformResourceOwnerException e) {
            fail("Expected InformClientException");
        } catch (InformClientException e) {
            assertThat(e.getCode(), is(exception.getCode()));
            assertThat(e.getDomainCause(), is(exception));
            assertThat(e.getRedirectURI(), is(expectedRedirectUri));
        }
    }

    @Test(expected=InformResourceOwnerException.class)
    public void invalidResponseTypeRedirectUriMismatchShouldThrowInformResourceOwnerException() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException, InformClientException, InformResourceOwnerException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = new ArrayList<>();
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdFactory.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriFactory.makeRedirectUri(redirectUris)).thenReturn(expectedRedirectUri);

        NoItemsError cause = new NoItemsError("");
        ResponseTypeException exception = new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_LIST, cause);
        when(mockResponseTypeFactory.makeResponseType(responseTypes)).thenThrow(exception);

        when(mockCompareClientRedirectUri.run(expectedUuid, expectedRedirectUri, exception)).thenThrow(InformResourceOwnerException.class);

        subject.make(clientIds, responseTypes, redirectUris, scopes, states);
    }

}