package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import helper.fixture.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitGrantAccessToken;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.GrantInput;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.security.RandomString;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by tommackenzie on 6/23/16.
 */
public class RequestAccessTokenTest {

    private RequestAccessToken subject;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private ValidateParams mockValidateParamsTokenResponseType;
    @Mock
    private ScopeRepository mockScopeRepository;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private IssueTokenImplicitGrant mockIssueTokenImplicitGrant;
    @Mock
    private ClientRepository mockClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new RequestAccessToken(mockLoginResourceOwner, mockValidateParamsTokenResponseType, mockScopeRepository, mockRandomString, mockIssueTokenImplicitGrant, mockClientRepository);
    }

    @Test
    public void requestTokenShouldReturnToken() throws Exception {

        UUID clientId = UUID.randomUUID();
        GrantInput grantInput = FixtureFactory.makeEmptyGrantInput();
        grantInput.getClientIds().add(clientId.toString());
        grantInput.getRedirectUris().add(FixtureFactory.makeSecureRedirectUri().toString());
        grantInput.getScopes().add("profile");
        grantInput.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.of(FixtureFactory.makeSecureRedirectUri()));
        authRequest.setScopes(grantInput.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        List<Scope> scopesToAddToToken = FixtureFactory.makeScopes();
        Token token = FixtureFactory.makeToken();

        when(mockValidateParamsTokenResponseType.run(
                grantInput.getClientIds(),
                grantInput.getResponseTypes(),
                grantInput.getRedirectUris(),
                grantInput.getScopes(),
                grantInput.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(grantInput.getUserName(), grantInput.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockScopeRepository.findByNames(authRequest.getScopes())).thenReturn(scopesToAddToToken);
        when(mockIssueTokenImplicitGrant.run(resourceOwner, scopesToAddToToken, accessToken)).thenReturn(token);
        when(mockIssueTokenImplicitGrant.getSecondsToExpiration()).thenReturn(3600L);

        ImplicitGrantAccessToken actual = subject.requestToken(grantInput);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(FixtureFactory.makeSecureRedirectUri()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getScope(), is(Optional.of("profile foo")));
        assertThat(actual.getState(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        verify(mockClientRepository, never()).getById(authRequest.getClientId());
    }

    @Test
    public void requestTokenWithNoRedirectUriShouldReturnToken() throws Exception {
        UUID clientId = UUID.randomUUID();
        GrantInput grantInput = FixtureFactory.makeEmptyGrantInput();
        grantInput.getClientIds().add(clientId.toString());
        grantInput.getScopes().add("profile");
        grantInput.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.empty());
        authRequest.setScopes(grantInput.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        List<Scope> scopesToAddToToken = FixtureFactory.makeScopes();
        Token token = FixtureFactory.makeToken();
        Client client = FixtureFactory.makeTokenClientWithScopes();

        when(mockValidateParamsTokenResponseType.run(
                grantInput.getClientIds(),
                grantInput.getResponseTypes(),
                grantInput.getRedirectUris(),
                grantInput.getScopes(),
                grantInput.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(grantInput.getUserName(), grantInput.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockScopeRepository.findByNames(authRequest.getScopes())).thenReturn(scopesToAddToToken);
        when(mockIssueTokenImplicitGrant.run(resourceOwner, scopesToAddToToken, accessToken)).thenReturn(token);
        when(mockIssueTokenImplicitGrant.getSecondsToExpiration()).thenReturn(3600L);

        when(mockClientRepository.getById(authRequest.getClientId())).thenReturn(client);

        ImplicitGrantAccessToken actual = subject.requestToken(grantInput);
        assertThat(actual, is(notNullValue()));

        assertThat(actual.getRedirectUri(), is(client.getRedirectURI()));
        assertThat(actual.getAccessToken(), is(accessToken));
        assertThat(actual.getScope(), is(Optional.of("profile foo")));
        assertThat(actual.getState(), is(Optional.empty()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));

        // should fetch client for redirect uri.
        verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
    }


    @Test
    public void requestTokenWithNoRedirectUriClientNotFoundShouldThrowInformResourceOwnerException() throws Exception {
        UUID clientId = UUID.randomUUID();
        GrantInput grantInput = FixtureFactory.makeEmptyGrantInput();
        grantInput.getClientIds().add(clientId.toString());
        grantInput.getScopes().add("profile");
        grantInput.getScopes().add("foo");

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(Optional.empty());
        authRequest.setScopes(grantInput.getScopes());
        authRequest.setState(Optional.empty());

        ResourceOwner resourceOwner = FixtureFactory.makeResourceOwner();
        String accessToken = "accessToken";
        List<Scope> scopesToAddToToken = FixtureFactory.makeScopes();
        Token token = FixtureFactory.makeToken();

        when(mockValidateParamsTokenResponseType.run(
                grantInput.getClientIds(),
                grantInput.getResponseTypes(),
                grantInput.getRedirectUris(),
                grantInput.getScopes(),
                grantInput.getStates()
        )).thenReturn(authRequest);

        when(mockLoginResourceOwner.run(grantInput.getUserName(), grantInput.getPlainTextPassword())).thenReturn(resourceOwner);
        when(mockRandomString.run()).thenReturn(accessToken);

        when(mockScopeRepository.findByNames(authRequest.getScopes())).thenReturn(scopesToAddToToken);
        when(mockIssueTokenImplicitGrant.run(resourceOwner, scopesToAddToToken, accessToken)).thenReturn(token);
        when(mockIssueTokenImplicitGrant.getSecondsToExpiration()).thenReturn(3600L);

        when(mockClientRepository.getById(authRequest.getClientId())).thenThrow(RecordNotFoundException.class);

        try {
            ImplicitGrantAccessToken actual = subject.requestToken(grantInput);
            fail("Expected to throw, InformResourceOwnerException");
        } catch (InformResourceOwnerException e) {
            verify(mockClientRepository, times(1)).getById(authRequest.getClientId());
            assertThat(e.getCode(), is(ErrorCode.CLIENT_NOT_FOUND.getCode()));
            assertThat(e.getDomainCause(), instanceOf(RecordNotFoundException.class));
            assertThat(e.getMessage(), is(ErrorCode.CLIENT_NOT_FOUND.getDescription()));
        }
    }
}