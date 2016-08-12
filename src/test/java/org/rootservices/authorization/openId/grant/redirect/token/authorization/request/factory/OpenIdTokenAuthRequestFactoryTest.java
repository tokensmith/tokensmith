package org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.validator.exception.EmptyValueError;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.OpenIdAuthRequestFactory;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.entity.OpenIdTokenAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.exception.NonceException;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.required.NonceFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 7/23/16.
 */
public class OpenIdTokenAuthRequestFactoryTest {
    @Mock
    private OpenIdAuthRequestFactory mockOpenIdAuthRequestFactory;
    @Mock
    private NonceFactory mockNonceFactory;

    private OpenIdTokenAuthRequestFactory subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new OpenIdTokenAuthRequestFactory(mockOpenIdAuthRequestFactory, mockNonceFactory);
    }

    @Test
    public void makeWhenNonceIsOkThenShouldBeOk() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());

        URI redirectUri = FixtureFactory.makeSecureRedirectUri();
        List<String> redirectUris = new ArrayList<>();
        redirectUris.add(redirectUri.toString());

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add("id_token");

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        List<String> states = new ArrayList<>();
        states.add("state");

        List<String> nonces = new ArrayList<>();
        nonces.add("nonce");

        when(mockNonceFactory.makeNonce(nonces)).thenReturn("nonce");

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest(clientId, responseTypes, redirectUri, scopes, Optional.of("state"));

        when(mockOpenIdAuthRequestFactory.make(clientIds, redirectUris, responseTypes, scopes, states)).thenReturn(openIdAuthRequest);

        OpenIdTokenAuthRequest actual = subject.make(clientIds, redirectUris, responseTypes, scopes, states, nonces);

        assertThat(actual.getClientId().toString(), is(clientId.toString()));
        assertThat(actual.getRedirectURI(), is(redirectUri));

        assertThat(actual.getResponseTypes(), is(notNullValue()));
        assertThat(actual.getResponseTypes().size(), is(1));
        assertThat(actual.getResponseTypes().get(0), is("id_token"));

        assertThat(actual.getScopes(), is(scopes));
        assertThat(actual.getState().isPresent(), is(true));
        assertThat(actual.getState().get(), is("state"));
        assertThat(actual.getNonce(), is("nonce"));
    }

    @Test
    public void makeWhenNonceInvalidThenShouldThrowInformResourceOwner() throws Exception{
        List<String> clientIds = new ArrayList<>();
        List<String> redirectUris = new ArrayList<>();
        List<String> responseTypes = new ArrayList<>();
        List<String> scopes = new ArrayList<>();
        List<String> states = new ArrayList<>();
        List<String> nonces = new ArrayList<>();
        nonces.add("nonce");

        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();
        when(mockOpenIdAuthRequestFactory.make(clientIds, redirectUris, responseTypes, scopes, states)).thenReturn(openIdAuthRequest);

        NonceException e = new NonceException(ErrorCode.NONCE_EMPTY_VALUE, new EmptyValueError(""));
        when(mockNonceFactory.makeNonce(nonces)).thenThrow(e);

        try {
            subject.make(clientIds, redirectUris, responseTypes, scopes, states, nonces);
            fail("Expected InformResourceOwnerException");
        } catch(InformResourceOwnerException actual){
            assertThat(actual.getCode(), is(ErrorCode.NONCE_EMPTY_VALUE.getCode()));
            assertThat(actual.getMessage(), is("nonce is invalid"));

            verify(mockOpenIdAuthRequestFactory, never()).make(clientIds, redirectUris, responseTypes, scopes, states);
        }
    }
}