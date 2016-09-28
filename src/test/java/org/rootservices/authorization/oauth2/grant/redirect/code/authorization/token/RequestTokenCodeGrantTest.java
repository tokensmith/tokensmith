package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.token;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.RequestTokenCodeGrant;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.oauth2.grant.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.oauth2.grant.token.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.token.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AuthCodeTokenRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Created by tommackenzie on 6/2/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class RequestTokenCodeGrantTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientOpendIdTokenReady;

    @Autowired
    private HashTextStaticSalt hashText;

    @Autowired
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private AuthCodeTokenRepository authCodeTokenRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RandomString randomString;

    @Autowired
    private RequestTokenCodeGrant subject;

    public Map<String, String> makeRequest(String code, URI redirectURI) {
        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "authorization_code");
        request.put("code", code);
        request.put("redirect_uri", redirectURI.toString());

        return request;
    }

    @Test
    @Transactional
    public void testRun() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );

        TokenResponse actual = subject.request(
                authCode.getAccessRequest().getClientUUID(),
                FixtureFactory.PLAIN_TEXT_PASSWORD,
                request
        );

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExtension(), is(Extension.NONE));

        // token should relate to a resource owner via, resource_owner_token
        String hashedCode = hashText.run(actual.getAccessToken());
        ResourceOwnerToken actualRot = resourceOwnerTokenRepository.getByAccessToken(hashedCode);

        assertThat(actualRot.getResourceOwner(), is(notNullValue()));
        assertThat(actualRot.getResourceOwner().getUuid(), is(authCode.getAccessRequest().getResourceOwnerUUID()));

        assertThat(actualRot.getToken(), is(notNullValue()));
        assertThat(actualRot.getToken().getGrantType(), is(GrantType.AUTHORIZATION_CODE));

        // token should have scopes via, token_scope
        assertThat(actualRot.getToken().getTokenScopes(), is(notNullValue()));
        assertThat(actualRot.getToken().getTokenScopes().size(), is(1));
        assertThat(actualRot.getToken().getTokenScopes().get(0).getScope().getName(), is("profile"));

        // token should relate to authorization code, via auth_code_token
        AuthCodeToken actualAct = authCodeTokenRepository.getByTokenId(actualRot.getToken().getUuid());
        assertThat(actualAct.getAuthCodeId(), is(authCode.getUuid()));
        assertThat(actualAct.getTokenId(), is(actualRot.getToken().getUuid()));
    }

    @Test
    @Transactional
    public void shouldBeOpenIdExtension() throws Exception {
        String plainTextAuthCode = randomString.run();

        AuthCode authCode = loadConfidentialClientOpendIdTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );

        TokenResponse actual = subject.request(
                authCode.getAccessRequest().getClientUUID(),
                FixtureFactory.PLAIN_TEXT_PASSWORD,
                request
        );

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getTokenType() , is(TokenType.BEARER));
        assertThat(actual.getExtension(), is(Extension.IDENTITY));

    }

    @Test
    @Transactional
    public void testRunLoginClientFails() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );


        UnauthorizedException expected = null;
        TokenResponse actual = null;
        try {
            subject.request(
                authCode.getAccessRequest().getClientUUID(),
                "wrong-password",
                request
            );
            fail("No exception was thrown. Expected UnauthorizedException");
        } catch (UnauthorizedException e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("CompromisedCodeException was thrown. Expected UnauthorizedException");
        } catch (AuthorizationCodeNotFound e) {
            fail("AuthorizationCodeNotFound was thrown. Expected UnauthorizedException");
        } catch (BadRequestException e) {
            fail("BadRequestException was thrown. Expected UnauthorizedException");
        }
        assertThat(expected, is(notNullValue()));
        assertThat(expected.getCode(), is(ErrorCode.PASSWORD_MISMATCH.getCode()));
        assertThat(expected.getMessage(), is(ErrorCode.PASSWORD_MISMATCH.getDescription()));
        assertThat(actual, is(nullValue()));
    }

    @Test
    @Transactional
    public void testMissingCodeExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );
        request.remove("code");

        BadRequestException expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("No exception was thrown. Expected BadRequestException");
        } catch (UnauthorizedException e) {
            fail("UnauthorizedException was thrown. Expected BadRequestException");
        } catch (BadRequestException e ) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("CompromisedCodeException was thrown. Expected BadRequestException");
        } catch (AuthorizationCodeNotFound e) {
            fail("AuthorizationCodeNotFound was thrown. Expected BadRequestException");
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getCode(), is(ErrorCode.MISSING_KEY.getCode()));
        assertThat(((MissingKeyException) expected.getDomainCause()).getKey(), is("code"));
        assertThat(expected.getError(), is("invalid_request"));
        assertThat(expected.getDescription(), is("code is a required field"));
        assertThat(actual, is(nullValue()));
    }
    
    @Test
    @Transactional
    public void testMissingRedirectUriExpectAuthorizationCodeNotFound() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );
        request.remove("redirect_uri");

        AuthorizationCodeNotFound expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("No exception was thrown. Expected AuthorizationCodeNotFound");
        } catch (UnauthorizedException e) {
            fail("UnauthorizedException was thrown. Expected AuthorizationCodeNotFound");
        } catch (BadRequestException e ) {
            fail("BadRequestException was thrown. Expected AuthorizationCodeNotFound");
        } catch (AuthorizationCodeNotFound e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("CompromisedCodeException was thrown. Expected AuthorizationCodeNotFound");
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getCode(), is(ErrorCode.REDIRECT_URI_MISMATCH.getCode()));
        assertThat(expected.getError(), is("invalid_grant"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    @Transactional
    public void testIsRevokedExpectAuthorizationCodeNotFound() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, true, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );

        AuthorizationCodeNotFound expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("No exception was thrown. Expected AuthorizationCodeNotFound");
        } catch (UnauthorizedException e) {
            fail("UnauthorizedException was thrown. Expected AuthorizationCodeNotFound");
        } catch (BadRequestException e ) {
            fail("BadRequestException was thrown. Expected AuthorizationCodeNotFound");
        } catch (AuthorizationCodeNotFound e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("CompromisedCodeException was thrown. Expected AuthorizationCodeNotFound");
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getCode(), is(ErrorCode.AUTH_CODE_NOT_FOUND.getCode()));
        assertThat(expected.getError(), is("invalid_grant"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    @Transactional
    public void testRedirectUriIsNotHttpsExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                new URI(FixtureFactory.REDIRECT_URI)
        );

        BadRequestException expected = null;
        TokenResponse actual = null;

        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("BadRequestException expected");
        } catch (UnauthorizedException e) {
            fail("BadRequestException expected");
        } catch (AuthorizationCodeNotFound e) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("BadRequestException expected");
        }

        assertThat(actual, is(nullValue()));
        assertThat(expected.getCode(), is(ErrorCode.REDIRECT_URI_INVALID.getCode()));
        assertThat(expected.getDescription(), is("redirect_uri is invalid"));
        assertThat(expected.getError(), is("invalid_request"));
        assertThat(expected.getDomainCause(), instanceOf(InvalidValueException.class));
    }

    @Test
    @Transactional
    public void testRedirectUriIsNotValidExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );
        request.put("redirect_uri", "foo");

        BadRequestException expected = null;
        TokenResponse actual = null;

        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("BadRequestException expected");
        } catch (UnauthorizedException e) {
            fail("BadRequestException expected");
        } catch (AuthorizationCodeNotFound e) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("BadRequestException expected");
        }

        assertThat(actual, is(nullValue()));
        assertThat(expected.getCode(), is(ErrorCode.REDIRECT_URI_INVALID.getCode()));
        assertThat(expected.getDescription(), is("redirect_uri is invalid"));
        assertThat(expected.getError(), is("invalid_request"));
        assertThat(expected.getDomainCause(), instanceOf(InvalidValueException.class));
    }

    @Test
    @Transactional
    public void shouldThrowBadRequestWhenPayloadHasUnknownKey() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );
        request.put("unknown_key", "banana");

        TokenResponse actual = null;
        BadRequestException expected = null;
        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
            fail("BadRequestException expected");
        } catch (UnauthorizedException e) {
            fail("BadRequestException expected");
        } catch (AuthorizationCodeNotFound e) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        } catch (CompromisedCodeException e) {
            fail("BadRequestException expected");
        }

        assertThat(actual, is(nullValue()));
        assertThat(expected.getCode(), is(ErrorCode.UNKNOWN_KEY.getCode()));
        assertThat(expected.getError(), is("invalid_request"));
        assertThat(expected.getDescription(), is("unknown_key is a unknown key"));
        assertThat(expected.getDomainCause(), instanceOf(UnknownKeyException.class));
    }

    /**
     * Cannot makeAuthCode in a transaction because the transaction fails when the
     * unique key constraint is observed.
     *
     * @throws DuplicateRecordException
     * @throws URISyntaxException
     */
    @Test
    public void runExpectCompromisedCodeException() throws Exception {

        // insert a token that relates to the auth code.
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeOpenIdToken();
        tokenRepository.insert(token);

        AuthCodeToken authCodeToken = new AuthCodeToken();
        authCodeToken.setId(UUID.randomUUID());
        authCodeToken.setAuthCodeId(authCode.getUuid());
        authCodeToken.setTokenId(token.getUuid());
        authCodeTokenRepository.insert(authCodeToken);
        // end - insert a token that relates to the auth code.

        Map<String, String> request = makeRequest(
                plainTextAuthCode,
                authCode.getAccessRequest().getRedirectURI().get()
        );

        CompromisedCodeException exception = null;
        TokenResponse actual = null;
        try {
            actual = subject.request(
                    authCode.getAccessRequest().getClientUUID(),
                    FixtureFactory.PLAIN_TEXT_PASSWORD,
                    request
            );
        } catch (UnauthorizedException e) {
            fail("actual UnauthorizedException, expected CompromisedCodeException");
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("actual AuthorizationCodeNotFound, expected CompromisedCodeException");
        } catch (BadRequestException e) {
            fail("actual BadRequestException expected CompromisedCodeException");
        } catch (CompromisedCodeException e) {
            exception = e;
        }

        assertThat(actual, is(nullValue()));
        assertThat(exception, is(notNullValue()));
        assertThat(exception.getCode(), is(ErrorCode.COMPROMISED_AUTH_CODE.getCode()));
        assertThat(exception.getError(), is("invalid_grant"));
        assertThat(exception.getDomainCause(), instanceOf(DuplicateRecordException.class));

        // make sure the first token was revoked.
        Token token1 = tokenRepository.getByAuthCodeId(authCode.getUuid());
        assertThat(token1.isRevoked(), is(true));

        // make sure the authorization code was revoked.
        AuthCode authCode1 = authCodeRepository.getById(authCode.getUuid());
        assertThat(authCode1.isRevoked(), is(true));
    }
}