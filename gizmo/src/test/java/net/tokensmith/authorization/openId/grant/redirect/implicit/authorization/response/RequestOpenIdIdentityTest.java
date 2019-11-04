package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenClaims;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitIdentity;
import net.tokensmith.authorization.openId.identity.MakeImplicitIdentityToken;
import net.tokensmith.authorization.openId.identity.exception.IdTokenException;
import net.tokensmith.authorization.openId.identity.exception.KeyNotFoundException;
import net.tokensmith.authorization.openId.identity.exception.ProfileNotFoundException;
import net.tokensmith.repository.entity.ResourceOwner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
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
        subject = new RequestOpenIdIdentity(
                mockValidateOpenIdIdImplicitGrant,
                mockLoginResourceOwner,
                mockMakeImplicitIdentityToken,
                "https://sso.rootservices.org"
        );
    }

    @Test
    public void requestShouldReturnIdentity() throws Exception {
        String responseType = "id_token";
        UUID clientId = UUID.randomUUID();

        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        Map<String, List<String>> params = FixtureFactory.makeOpenIdParameters(clientId, responseType);

        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        String idToken = "encoded-jwt";

        when(mockValidateOpenIdIdImplicitGrant.run(params)).thenReturn(request);

        when(mockLoginResourceOwner.run(userName, password)).thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                eq(request.getNonce()), tcArgumentCaptor.capture(), eq(ro), eq(request.getScopes()))
        ).thenReturn(idToken);

        OpenIdImplicitIdentity actual = subject.request(userName, password, params);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getIdToken(), is(idToken));
        assertThat(actual.getRedirectUri(), is(request.getRedirectURI()));
        assertThat(actual.getState(), is(Optional.of("state")));
        assertThat(actual.getScope(), is(Optional.empty()));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(notNullValue()));
    }

    @Test
    public void requestWhenProfileNotFoundShouldThrowInformClientException() throws Exception {
        String responseType = "id_token";
        UUID clientId = UUID.randomUUID();

        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        Map<String, List<String>> params = FixtureFactory.makeOpenIdParameters(clientId, responseType);

        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        ProfileNotFoundException pnfe = new ProfileNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(params)).thenReturn(request);

        when(mockLoginResourceOwner.run(userName, password))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                eq(request.getNonce()), tcArgumentCaptor.capture(), eq(ro), eq(request.getScopes()))
        ).thenThrow(pnfe);

        InformClientException expected = null;
        try {
            subject.request(userName, password, params);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.PROFILE_NOT_FOUND.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.PROFILE_NOT_FOUND.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getCause(), instanceOf(ProfileNotFoundException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(notNullValue()));
    }

    @Test
    public void requestWhenKeyNotFoundShouldThrowInformClientException() throws Exception {
        String responseType = "id_token";
        UUID clientId = UUID.randomUUID();

        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        Map<String, List<String>> params = FixtureFactory.makeOpenIdParameters(clientId, responseType);

        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        KeyNotFoundException knfe = new KeyNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(params)).thenReturn(request);

        when(mockLoginResourceOwner.run(userName, password))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                eq(request.getNonce()), tcArgumentCaptor.capture(), eq(ro), eq(request.getScopes()))
        ).thenThrow(knfe);

        InformClientException expected = null;
        try {
            subject.request(userName, password, params);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.SIGN_KEY_NOT_FOUND.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.SIGN_KEY_NOT_FOUND.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getCause(), instanceOf(KeyNotFoundException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(notNullValue()));
    }

    @Test
    public void requestWhenJwtEncodingErrorShouldThrowInformClientException() throws Exception {
        String responseType = "id_token";
        UUID clientId = UUID.randomUUID();

        String userName = FixtureFactory.makeRandomEmail();
        String password = FixtureFactory.PLAIN_TEXT_PASSWORD;
        Map<String, List<String>> params = FixtureFactory.makeOpenIdParameters(clientId, responseType);

        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);
        IdTokenException ide = new IdTokenException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(params)).thenReturn(request);

        when(mockLoginResourceOwner.run(userName, password))
                .thenReturn(ro);

        when(mockMakeImplicitIdentityToken.makeIdentityOnly(
                eq(request.getNonce()), tcArgumentCaptor.capture(), eq(ro), eq(request.getScopes()))
        ).thenThrow(ide);

        InformClientException expected = null;
        try {
            subject.request(userName, password, params);
        } catch (InformClientException actual) {
            expected = actual;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getError(), is("server_error"));
        assertThat(expected.getDescription(), is(ErrorCode.JWT_ENCODING_ERROR.getDescription()));
        assertThat(expected.getCode(), is(ErrorCode.JWT_ENCODING_ERROR.getCode()));
        assertThat(expected.getRedirectURI(), is(request.getRedirectURI()));
        assertThat(expected.getState(), is(request.getState()));
        assertThat(expected.getCause(), instanceOf(IdTokenException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(notNullValue()));
    }

}