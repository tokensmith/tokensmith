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
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
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
    private IssueTokenImplicitGrant mockIssueTokenImplicitGrant;
    @Mock
    private MakeImplicitIdentityToken mockMakeImplicitIdentityToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestOpenIdImplicitTokenAndIdentity(
                mockValidateOpenIdIdImplicitGrant,
                mockLoginResourceOwner,
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

        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph();
        tokenGraph.getToken().setCreatedAt(OffsetDateTime.now());

        List<String> scopesForIdToken = tokenGraph.getToken().getTokenScopes().stream()
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
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes())).thenReturn(tokenGraph);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(tokenGraph.getPlainTextAccessToken()), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
        ).thenReturn(idToken);

        OpenIdImplicitAccessToken actual = subject.request(input);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(tokenGraph.getPlainTextAccessToken()));
        assertThat(actual.getExpiresIn(), is(tokenGraph.getToken().getSecondsToExpiration()));
        assertThat(actual.getIdToken(), is(idToken));
        assertThat(actual.getRedirectUri(), is(request.getRedirectURI()));
        assertThat(actual.getState(), is(Optional.of("state")));
        assertThat(actual.getScope(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(tokenGraph.getToken().getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
    }

    @Test
    public void requestWhenProfileNotFoundShouldThrowInformClientException() throws Exception {

        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph();
        tokenGraph.getToken().setCreatedAt(OffsetDateTime.now());

        List<String> scopesForIdToken = tokenGraph.getToken().getTokenScopes().stream()
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
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes())).thenReturn(tokenGraph);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(tokenGraph.getPlainTextAccessToken()), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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
        assertThat(expected.getCause(), instanceOf(ProfileNotFoundException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(tokenGraph.getToken().getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
    }

    @Test
    public void requestWhenKeyNotFoundShouldThrowInformClientException() throws Exception {

        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph();
        tokenGraph.getToken().setCreatedAt(OffsetDateTime.now());

        List<String> scopesForIdToken = tokenGraph.getToken().getTokenScopes().stream()
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
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes())).thenReturn(tokenGraph);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(tokenGraph.getPlainTextAccessToken()), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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
        assertThat(expected.getCause(), instanceOf(KeyNotFoundException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(tokenGraph.getToken().getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
    }

    @Test
    public void requestWhenJwtEncodingErrorShouldThrowInformClientException() throws Exception {
        String responseType = "token id_token";
        UUID clientId = UUID.randomUUID();
        OpenIdInputParams input = FixtureFactory.makeOpenIdInputParams(clientId, responseType);
        OpenIdImplicitAuthRequest request = FixtureFactory.makeOpenIdImplicitAuthRequest(clientId);

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        TokenGraph tokenGraph = FixtureFactory.makeImplicitTokenGraph();
        tokenGraph.getToken().setCreatedAt(OffsetDateTime.now());

        List<String> scopesForIdToken = tokenGraph.getToken().getTokenScopes().stream()
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
        when(mockIssueTokenImplicitGrant.run(request.getClientId(), resourceOwner, request.getScopes())).thenReturn(tokenGraph);
        when(mockMakeImplicitIdentityToken.makeForAccessToken(
                eq(tokenGraph.getPlainTextAccessToken()), eq(request.getNonce()), tcArgumentCaptor.capture(), eq(resourceOwner.getId()), eq(scopesForIdToken))
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
        assertThat(expected.getCause(), instanceOf(IdTokenException.class));

        assertThat(tcArgumentCaptor.getValue().getIssuer(), is("https://sso.rootservices.org"));
        assertThat(tcArgumentCaptor.getValue().getAudience(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getAudience().size(), is(1));
        assertThat(tcArgumentCaptor.getValue().getAudience().get(0), is(request.getClientId().toString()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(notNullValue()));
        assertThat(tcArgumentCaptor.getValue().getIssuedAt(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getExpirationTime(), is(tokenGraph.getToken().getExpiresAt().toEpochSecond()));
        assertThat(tcArgumentCaptor.getValue().getAuthTime(), is(tokenGraph.getToken().getCreatedAt().toEpochSecond()));
    }
}