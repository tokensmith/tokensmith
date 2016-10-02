package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.openId.identity.MakeImplicitIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.persistence.entity.ResourceOwner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/13/16.
 */
public class RequestOpenIdIdentityTest {
    private RequestOpenIdIdentity subject;

    @Mock
    private ValidateOpenIdIdImplicitGrant mockValidateOpenIdIdImplicitGrant;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private MakeImplicitIdentityToken mockMakeImplicitIdentityToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestOpenIdIdentity(mockValidateOpenIdIdImplicitGrant, mockLoginResourceOwner, mockMakeImplicitIdentityToken);
    }

    @Test
    public void requestShouldReturnIdentity() throws Exception {
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams("id_token");
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        String idToken = "encoded-jwt";

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates(),
                input.getNonces()
        )).thenReturn(request);

        when(mockLoginResourceOwner.run(input.getUserName(), input.getPlainTextPassword()))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), ro.getId(), request.getScopes())
        ).thenReturn(idToken);

        OpenIdImplicitIdentity actual = subject.request(input);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getIdToken(), is(idToken));
        assertThat(actual.getRedirectUri(), is(request.getRedirectURI()));
        assertThat(actual.getState(), is(Optional.of("state")));
        assertThat(actual.getScope(), is(Optional.empty()));
    }

    @Test
    public void requestWhenProfileNotFoundShouldThrowInformClientException() throws Exception {
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams("id_token");
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ProfileNotFoundException pnfe = new ProfileNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates(),
                input.getNonces()
        )).thenReturn(request);

        when(mockLoginResourceOwner.run(input.getUserName(), input.getPlainTextPassword()))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), ro.getId(), request.getScopes())
        ).thenThrow(pnfe);

        InformClientException expected = null;
        try {
            subject.request(input);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.PROFILE_NOT_FOUND.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.PROFILE_NOT_FOUND.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getDomainCause(), instanceOf(ProfileNotFoundException.class));
    }

    @Test
    public void requestWhenKeyNotFoundShouldThrowInformClientException() throws Exception {
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams("id_token");
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        KeyNotFoundException knfe = new KeyNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates(),
                input.getNonces()
        )).thenReturn(request);

        when(mockLoginResourceOwner.run(input.getUserName(), input.getPlainTextPassword()))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), ro.getId(), request.getScopes())
        ).thenThrow(knfe);

        InformClientException expected = null;
        try {
            subject.request(input);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.SIGN_KEY_NOT_FOUND.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.SIGN_KEY_NOT_FOUND.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getDomainCause(), instanceOf(KeyNotFoundException.class));
    }

    @Test
    public void requestWhenJwtEncodingErrorShouldThrowInformClientException() throws Exception {
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams("id_token");
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest();
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        IdTokenException ide = new IdTokenException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates(),
                input.getNonces()
        )).thenReturn(request);

        when(mockLoginResourceOwner.run(input.getUserName(), input.getPlainTextPassword()))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                request.getNonce(), ro.getId(), request.getScopes())
        ).thenThrow(ide);

        InformClientException expected = null;
        try {
            subject.request(input);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.JWT_ENCODING_ERROR.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.JWT_ENCODING_ERROR.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getDomainCause(), instanceOf(IdTokenException.class));
    }

}