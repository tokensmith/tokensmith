package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.IssueTokenImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.ValidateOpenIdIdImplicitGrant;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.builder.OpenIdImplicitAccessTokenBuilder;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity.OpenIdImplicitAccessToken;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.openId.identity.MakeImplicitIdentityToken;
import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.RandomString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/6/16.
 */
public class RequestOpenIdImplicitTokenAndIdentityTest {

    private RequestOpenIdImplicitTokenAndIdentity subject;

    @Mock
    private ValidateOpenIdIdImplicitGrant mockValidateOpenIdIdImplicitGrant;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private IssueTokenImplicitGrant mockIssueTokenImplicitGrant;
    @Mock
    private MakeImplicitIdentityToken mockMakeImplicitIdentityToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestOpenIdImplicitTokenAndIdentity(
                mockValidateOpenIdIdImplicitGrant,
                mockLoginResourceOwner,
                mockRandomString,
                mockIssueTokenImplicitGrant,
                mockMakeImplicitIdentityToken,
                new OpenIdImplicitAccessTokenBuilder(),
                "https://sso.rootservices.org"
        );
    }

    @Test
    public void requestShouldReturnToken() throws Exception {
        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken(accessToken);
        token.setCreatedAt(OffsetDateTime.now());

        token.setSecondsToExpiration(3600L);

        List<String> scopesForIdToken = token.getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);

        String idToken = "encoded-jwt";

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        )).thenReturn(request);
        when(mockLoginResourceOwner.run(
                input.getUserName(), input.getPlainTextPassword())
        ).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(), accessToken)).thenReturn(token);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(accessToken), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
        ).thenReturn(idToken);

        OpenIdImplicitAccessToken actual = subject.request(input);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getExpiresIn(), is(token.getSecondsToExpiration()));
        assertThat(actual.getIdToken(), is(idToken));
        assertThat(actual.getRedirectUri(), is(request.getRedirectURI()));
        assertThat(actual.getState(), is(Optional.of("state")));
        assertThat(actual.getScope(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));

        // TODO: 130584847 assertions for tcArgumentCaptor
    }

    @Test
    public void requestWhenProfileNotFoundShouldThrowInformClientException() throws Exception {

        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken("access-token");
        token.setCreatedAt(OffsetDateTime.now());
        token.setSecondsToExpiration(3600L);

        List<String> scopesForIdToken = token.getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);

        ProfileNotFoundException pnfe = new ProfileNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        )).thenReturn(request);
        when(mockLoginResourceOwner.run(
                input.getUserName(), input.getPlainTextPassword())
        ).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(), accessToken)).thenReturn(token);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(accessToken), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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

        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken("access-token");
        token.setCreatedAt(OffsetDateTime.now());
        token.setSecondsToExpiration(3600L);

        List<String> scopesForIdToken = token.getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);

        KeyNotFoundException knfe = new KeyNotFoundException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        )).thenReturn(request);
        when(mockLoginResourceOwner.run(
                input.getUserName(), input.getPlainTextPassword())
        ).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(), accessToken)).thenReturn(token);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(accessToken), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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
        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "access-token";
        Token token = FixtureFactory.makeOpenIdToken("access-token");
        token.setCreatedAt(OffsetDateTime.now());
        token.setSecondsToExpiration(3600L);

        List<String> scopesForIdToken = token.getTokenScopes().stream()
                .map(item -> item.getScope().getName())
                .collect(Collectors.toList());

        ArgumentCaptor<TokenClaims> tcArgumentCaptor = ArgumentCaptor.forClass(TokenClaims.class);

        IdTokenException ide = new IdTokenException("", null);

        when(mockValidateOpenIdIdImplicitGrant.run(
                input.getClientIds(), input.getResponseTypes(), input.getRedirectUris(), input.getScopes(), input.getStates(), input.getNonces()
        )).thenReturn(request);
        when(mockLoginResourceOwner.run(
                input.getUserName(), input.getPlainTextPassword())
        ).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes(), accessToken)).thenReturn(token);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(accessToken), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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