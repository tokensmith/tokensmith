package net.tokensmith.authorization.oauth2.grant.refresh;

import helper.fixture.persistence.openid.LoadOpenIdConfClientAll;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.exception.BadRequestException;
import net.tokensmith.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import net.tokensmith.authorization.oauth2.grant.token.entity.Extension;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.oauth2.grant.token.exception.*;
import net.tokensmith.authorization.persistence.entity.AuthCode;
import net.tokensmith.authorization.persistence.entity.RefreshToken;
import net.tokensmith.authorization.persistence.entity.Scope;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 10/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
@Transactional
public class RequestTokenRefreshGrantTest {
    @Autowired
    private RequestTokenRefreshGrant subject;
    @Autowired
    private LoadOpenIdConfClientAll loadOpenIdConfClientAll;

    @Test
    public void requestShouldBeOk() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);

        TokenResponse actual = subject.request(clientId, "password", request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRefreshAccessToken(), is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));
    }

    @Test
    public void requestWhenMissingKeyShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");

        BadRequestException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("refresh_token is a required field"));
        assertThat(actual.getCause(), is(instanceOf(MissingKeyException.class)));
        assertThat(actual.getCode(), is(ErrorCode.MISSING_KEY.getCode()));
    }

    @Test
    public void requestWhenInvalidValueShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);
        request.put("scope", "");

        BadRequestException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("scope is invalid"));
        assertThat(actual.getCause(), is(instanceOf(InvalidValueException.class)));
        assertThat(actual.getCode(), is(ErrorCode.EMPTY_VALUE.getCode()));
    }

    @Test
    public void requestWhenUnknownKeyShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);
        request.put("foo", "");

        BadRequestException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is("foo is a unknown key"));
        assertThat(actual.getCause(), is(instanceOf(UnknownKeyException.class)));
        assertThat(actual.getCode(), is(ErrorCode.UNKNOWN_KEY.getCode()));
    }

    @Test
    public void requestWhenRefreshTokenNotFoundShould() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", "not-the-real-refresh-token");

        NotFoundException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(NotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("refresh token was not found"));
        assertThat(actual.getError(), is("invalid_grant"));
        assertThat(actual.getDescription(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getDescription()));
        assertThat(actual.getCause(), is(instanceOf(RecordNotFoundException.class)));
        assertThat(actual.getCode(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void requestWhenExtraScopesShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);
        request.put("scope", "foo");

        BadRequestException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_scope"));
        assertThat(actual.getDescription(), is("scope is not available for this client"));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getCode(), is(ErrorCode.SCOPES_NOT_SUPPORTED.getCode()));
    }

    @Test
    public void requestRefreshTokenDoesNotBelongToClientShouldThrowNotFoundException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);

        // load another confidential client.. that doesn't belong to the refresh token ^^
        String plainTextAuthCode2 = "plain-text-auth-code2";
        AuthCode authCode2 = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode2);
        UUID clentId2 = authCode2.getAccessRequest().getClientId();

        NotFoundException actual = null;
        try {
            subject.request(clentId2, "password", request);
        } catch(NotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("refresh token was not found"));
        assertThat(actual.getError(), is("invalid_grant"));
        assertThat(actual.getDescription(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getDescription()));
        assertThat(actual.getCause(), is(instanceOf(RecordNotFoundException.class)));
        assertThat(actual.getCode(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void requestWhenResourceOwnerNotLinkedToRefreshTokenShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForClient(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);

        NotFoundException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(NotFoundException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("no resource owner was associated to refresh token"));
        assertThat(actual.getError(), is("invalid_grant"));
        assertThat(actual.getDescription(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getDescription()));
        assertThat(actual.getCause(), is(instanceOf(RecordNotFoundException.class)));
        assertThat(actual.getCode(), is(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void requestWhenCompromisedRefreshTokenShouldThrowBadRequestException() throws Exception {
        String plainTextAuthCode = "plain-text-auth-code";
        AuthCode authCode = loadOpenIdConfClientAll.loadAuthCode(plainTextAuthCode);
        UUID clientId = authCode.getAccessRequest().getClientId();
        UUID resourceOwnerId = authCode.getAccessRequest().getResourceOwnerId();

        List<Scope> scopesForToken = authCode.getAccessRequest().getAccessRequestScopes().stream()
                .map(item -> item.getScope())
                .collect(Collectors.toList());

        String refreshAccessToken = "refresh-access-token";
        OffsetDateTime tokenExpirationAt = OffsetDateTime.now().minusDays(1);
        RefreshToken refreshToken = loadOpenIdConfClientAll.loadRefreshTokenForResourceOwner(
                refreshAccessToken, tokenExpirationAt, authCode.getId(), clientId, resourceOwnerId, scopesForToken
        );

        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("refresh_token", refreshAccessToken);

        TokenResponse tr = subject.request(clientId, "password", request);
        assertThat(tr, is(notNullValue()));

        // call it again and it should be compromised.
        BadRequestException actual = null;
        try {
            subject.request(clientId, "password", request);
        } catch(BadRequestException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Bad request"));
        assertThat(actual.getError(), is("invalid_grant"));
        assertThat(actual.getDescription(), is("the refresh token was already used"));
        assertThat(actual.getCause(), instanceOf(CompromisedRefreshTokenException.class));
        assertThat(actual.getCode(), is(ErrorCode.COMPROMISED_REFRESH_TOKEN.getCode()));
    }
}