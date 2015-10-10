package org.rootservices.authorization.grant.code.openid.protocol.authorization.request.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.openid.protocol.authorization.request.CompareClientRedirectUri;
import org.rootservices.authorization.grant.code.openid.protocol.authorization.request.builder.required.OpenIdRedirectUriBuilder;
import org.rootservices.authorization.grant.code.openid.protocol.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.*;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional.ScopesBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional.StateBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ClientIdBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ResponseTypeBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.EmptyValueError;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.validator.exception.NoItemsError;
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
@RunWith(MockitoJUnitRunner.class)
public class OpenIdAuthRequestBuilderImplTest {

    @Mock
    private ClientIdBuilder mockClientIdBuilder;
    @Mock
    private OpenIdRedirectUriBuilder mockOpenIdRedirectUriBuilder;
    @Mock
    private ResponseTypeBuilder mockResponseTypeBuilder;
    @Mock
    private ScopesBuilder mockScopesBuilder;
    @Mock
    private StateBuilder mockStateBuilder;
    @Mock
    private CompareClientRedirectUri mockCompareClientRedirectUri;
    private OpenIdAuthRequestBuilder subject;

    @Before
    public void setUp() {
        subject = new OpenIdAuthRequestBuilderImpl(
                mockClientIdBuilder,
                mockOpenIdRedirectUriBuilder,
                mockResponseTypeBuilder,
                mockScopesBuilder,
                mockStateBuilder,
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
    public void shouldBuild() throws URISyntaxException, ClientIdException, RedirectUriException, ResponseTypeException, ScopesException, StateException, InformClientException, InformResourceOwnerException {
        UUID expectedUuid = UUID.randomUUID();
        URI expectedRedirectUri = makeRedirectUri();
        ResponseType expectedResponseType = makeResponseType();
        Optional<String> expectedStates = makeStates();

        List<String> clientIds = buildList(expectedUuid);
        List<String> redirectUris = buildList(expectedRedirectUri);
        List<String> responseTypes = buildList(expectedResponseType);
        List<String> scopes = makeScopes();
        List<String> states = buildListFromOptional(expectedStates);

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeBuilder.makeResponseType(responseTypes)).thenReturn(expectedResponseType);
        when(mockScopesBuilder.makeScopes(scopes)).thenReturn(scopes);
        when(mockStateBuilder.makeState(states)).thenReturn(expectedStates);

        OpenIdAuthRequest actual = subject.build(
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
        when(mockClientIdBuilder.makeClientId(clientIds)).thenThrow(exception);

        try {
            subject.build(clientIds, responseTypes, redirectUris, scopes, states);
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

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);

        NoItemsError cause = new NoItemsError("");
        RedirectUriException exception = new RedirectUriException(ErrorCode.REDIRECT_URI_EMPTY_LIST, cause);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenThrow(exception);

        try {
            subject.build(clientIds, responseTypes, redirectUris, scopes, states);
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

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenReturn(expectedRedirectUri);

        NoItemsError cause = new NoItemsError("");
        ResponseTypeException exception = new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_LIST, cause);
        when(mockResponseTypeBuilder.makeResponseType(responseTypes)).thenThrow(exception);

        try {
            subject.build(clientIds, responseTypes, redirectUris, scopes, states);
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

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeBuilder.makeResponseType(responseTypes)).thenReturn(expectedResponseType);

        EmptyValueError cause = new EmptyValueError("");
        ScopesException exception = new ScopesException(ErrorCode.SCOPES_EMPTY_VALUE, "invalid_scope", cause);
        when(mockScopesBuilder.makeScopes(scopes)).thenThrow(exception);

        try {
            subject.build(clientIds, responseTypes, redirectUris, scopes, states);
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

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenReturn(expectedRedirectUri);
        when(mockResponseTypeBuilder.makeResponseType(responseTypes)).thenReturn(expectedResponseType);
        when(mockScopesBuilder.makeScopes(scopes)).thenReturn(scopes);

        EmptyValueError cause = new EmptyValueError("");
        StateException exception = new StateException(ErrorCode.STATE_EMPTY_VALUE, cause);

        when(mockStateBuilder.makeState(states)).thenThrow(exception);

        try {
            subject.build(clientIds, responseTypes, redirectUris, scopes, states);
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

        when(mockClientIdBuilder.makeClientId(clientIds)).thenReturn(expectedUuid);
        when(mockOpenIdRedirectUriBuilder.build(redirectUris)).thenReturn(expectedRedirectUri);

        NoItemsError cause = new NoItemsError("");
        ResponseTypeException exception = new ResponseTypeException(ErrorCode.RESPONSE_TYPE_EMPTY_LIST, cause);
        when(mockResponseTypeBuilder.makeResponseType(responseTypes)).thenThrow(exception);

        when(mockCompareClientRedirectUri.run(expectedUuid, expectedRedirectUri, exception)).thenThrow(InformResourceOwnerException.class);

        subject.build(clientIds, responseTypes, redirectUris, scopes, states);
    }

}