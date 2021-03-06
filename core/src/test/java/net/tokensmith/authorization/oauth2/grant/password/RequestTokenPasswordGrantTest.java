package net.tokensmith.authorization.oauth2.grant.password;

import helper.fixture.FixtureFactory;
import net.tokensmith.authorization.authenticate.LoginConfidentialClient;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import net.tokensmith.authorization.oauth2.grant.password.factory.TokenInputPasswordGrantFactory;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.exception.InvalidValueException;
import net.tokensmith.authorization.oauth2.grant.token.exception.MissingKeyException;
import net.tokensmith.authorization.oauth2.grant.token.exception.UnknownKeyException;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.Scope;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by tommackenzie on 9/18/16.
 */
public class RequestTokenPasswordGrantTest {
    private RequestTokenPasswordGrant subject;

    @Mock
    private LoginConfidentialClient mockLoginConfidentialClient;
    @Mock
    private TokenInputPasswordGrantFactory mockTokenInputPasswordGrantFactory;
    @Mock
    private LoginResourceOwner mockLoginResourceOwner;
    @Mock
    private RandomString mockRandomString;
    @Mock
    private IssueTokenPasswordGrant mockIssueTokenPasswordGrant;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        subject = new RequestTokenPasswordGrant(
                mockLoginConfidentialClient,
                mockTokenInputPasswordGrantFactory,
                mockLoginResourceOwner,
                mockRandomString,
                mockIssueTokenPasswordGrant
        );
    }

    @Test
    public void requestWhenOAuthTokenShouldHaveNoExtension() throws Exception {
        Client client = FixtureFactory.makePasswordClientWithScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(client);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        List<Client> audience = FixtureFactory.makeAudience(client);

        Map<String, String> request =  new HashMap<>();
        request.put("grant_type", "password");
        request.put("username", ro.getEmail());
        request.put("password", FixtureFactory.PLAIN_TEXT_PASSWORD);
        request.put("scope", "profile");

        TokenInputPasswordGrant input = new TokenInputPasswordGrant();
        input.setScopes(client.getScopes().stream().map(item -> item.getName()).collect(Collectors.toList()));
        input.setUserName(ro.getEmail());
        input.setPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse();

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenReturn(input);

        when(mockLoginResourceOwner.run(input.getUserName(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(ro);

        when(mockRandomString.run()).thenReturn("access-token");

        when(mockIssueTokenPasswordGrant.run(cc.getClient().getId(), ro.getId(), client.getScopes(), audience, Optional.empty()))
                .thenReturn(tokenResponse);

        TokenResponse actual = subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
        assertThat(actual, is(tokenResponse));

    }

    @Test
    public void requestWhenOpenIdTokenShouldHaveIdentityExtension() throws Exception {
        Client openIdClient = FixtureFactory.makePasswordClientWithOpenIdScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(openIdClient);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        List<Client> audience = FixtureFactory.makeAudience(openIdClient);

        Map<String, String> request =  new HashMap<>();
        request.put("grant_type", "password");
        request.put("username", ro.getEmail());
        request.put("password", FixtureFactory.PLAIN_TEXT_PASSWORD);
        request.put("scope", "profile openid");

        TokenInputPasswordGrant input = new TokenInputPasswordGrant();
        input.setScopes(openIdClient.getScopes().stream().map(item -> item.getName()).collect(Collectors.toList()));
        input.setUserName(ro.getEmail());
        input.setPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse();

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenReturn(input);

        when(mockLoginResourceOwner.run(input.getUserName(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(ro);

        when(mockRandomString.run()).thenReturn("access-token");

        when(mockIssueTokenPasswordGrant.run(cc.getClient().getId(), ro.getId(), openIdClient.getScopes(), audience, Optional.empty()))
                .thenReturn(tokenResponse);

        TokenResponse actual = subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(tokenResponse));
    }

    @Test
    public void requestWhenUnknownKeyExceptionShouldThrowBadRequestException() throws Exception {
        Client client = FixtureFactory.makePasswordClientWithScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(client);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        Map<String, String> request =  new HashMap<>();

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        UnknownKeyException uke = new UnknownKeyException(
                ErrorCode.UNKNOWN_KEY.getDescription(),
                "foo",
                ErrorCode.UNKNOWN_KEY.getCode()
        );

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenThrow(uke);

        BadRequestException actual = null;
        try{
            subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getCode(), is(ErrorCode.UNKNOWN_KEY.getCode()));
        assertThat(actual.getDescription(), is("foo is a unknown key"));
        assertThat(actual.getCause(), is(uke));
    }

    @Test
    public void requestWhenInvalidValueExceptionShouldThrowBadRequestException() throws Exception {
        Client client = FixtureFactory.makePasswordClientWithScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(client);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        Map<String, String> request =  new HashMap<>();

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        InvalidValueException ive = new InvalidValueException(
                ErrorCode.SCOPES_EMPTY_VALUE.getDescription(),
                ErrorCode.SCOPES_EMPTY_VALUE.getCode(),
                "scope",
                ""
        );

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenThrow(ive);

        BadRequestException actual = null;
        try{
            subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getCode(), is(ErrorCode.SCOPES_EMPTY_VALUE.getCode()));
        assertThat(actual.getDescription(), is("scope is invalid"));
        assertThat(actual.getCause(), is(ive));
    }

    @Test
    public void requestWhenMissingKeyExceptionShouldThrowBadRequestException() throws Exception {
        Client client = FixtureFactory.makePasswordClientWithScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(client);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();

        Map<String, String> request =  new HashMap<>();

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        MissingKeyException mke = new MissingKeyException(
                "missing key password",
                "password"
        );

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenThrow(mke);

        BadRequestException actual = null;
        try{
            subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getCode(), is(ErrorCode.MISSING_KEY.getCode()));
        assertThat(actual.getDescription(), is("password is a required field"));
        assertThat(actual.getCause(), is(mke));
    }



    @Test
    public void matchScopesMatchesAllShouldBeOk() throws Exception {
        List<Scope> scopes = FixtureFactory.makeScopes();
        scopes.add(FixtureFactory.makeScope("email"));

        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("email");
        scopeNames.add("profile");

        List<Scope> actual = subject.matchScopes(scopes, scopeNames);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0).getId(), is(notNullValue()));
        assertThat(actual.get(0).getName(), is("email"));
        assertThat(actual.get(1).getId(), is(notNullValue()));
        assertThat(actual.get(1).getName(), is("profile"));

    }

    @Test
    public void matchScopesMatchesSubsetShouldBeOk() throws Exception {
        List<Scope> scopes = FixtureFactory.makeScopes();
        scopes.add(FixtureFactory.makeScope("email"));

        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("email");

        List<Scope> actual = subject.matchScopes(scopes, scopeNames);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getId(), is(notNullValue()));
        assertThat(actual.get(0).getName(), is("email"));
    }

    @Test
    public void matchScopesMissingOneShouldThrowBadRequestException() throws Exception {
        List<Scope> scopes = FixtureFactory.makeScopes();
        scopes.add(FixtureFactory.makeScope("email"));

        List<String> scopeNames = new ArrayList<>();
        scopeNames.add("email");
        scopeNames.add("profile");
        scopeNames.add("notFound");

        BadRequestException actual = null;
        try {
            subject.matchScopes(scopes, scopeNames);
        } catch (BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_scope"));
        assertThat(actual.getDescription(), is("scope is not available for this client"));
        assertThat(actual.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
    }

    @Test(expected = ServerException.class)
    public void requestShouldThrowServerException() throws Exception {
        Client openIdClient = FixtureFactory.makePasswordClientWithOpenIdScopes();
        ConfidentialClient cc = FixtureFactory.makeConfidentialClient(openIdClient);
        ResourceOwner ro = FixtureFactory.makeResourceOwner();
        List<Client> audience = FixtureFactory.makeAudience(openIdClient);

        Map<String, String> request =  new HashMap<>();
        request.put("grant_type", "password");
        request.put("username", ro.getEmail());
        request.put("password", FixtureFactory.PLAIN_TEXT_PASSWORD);
        request.put("scope", "profile openid");

        TokenInputPasswordGrant input = new TokenInputPasswordGrant();
        input.setScopes(openIdClient.getScopes().stream().map(item -> item.getName()).collect(Collectors.toList()));
        input.setUserName(ro.getEmail());
        input.setPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        when(mockLoginConfidentialClient.run(cc.getId(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(cc);

        when(mockTokenInputPasswordGrantFactory.run(request))
                .thenReturn(input);

        when(mockLoginResourceOwner.run(input.getUserName(), FixtureFactory.PLAIN_TEXT_PASSWORD))
                .thenReturn(ro);

        when(mockRandomString.run()).thenReturn("access-token");

        when(mockIssueTokenPasswordGrant.run(cc.getClient().getId(), ro.getId(), openIdClient.getScopes(), audience, Optional.empty()))
                .thenThrow(ServerException.class);

        subject.request(cc.getId(),FixtureFactory.PLAIN_TEXT_PASSWORD, request);
    }
}