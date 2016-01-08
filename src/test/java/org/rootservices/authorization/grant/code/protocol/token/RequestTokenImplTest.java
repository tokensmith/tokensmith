package org.rootservices.authorization.grant.code.protocol.token;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;
import org.rootservices.authorization.grant.code.protocol.token.exception.CompromisedCodeException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.*;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URISyntaxException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;


/**
 * Created by tommackenzie on 6/2/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class RequestTokenImplTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RandomString randomString;

    @Autowired
    private RequestToken subject;

    @Test
    public void testRun() throws Exception {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"code\": \""+ plainTextAuthCode + "\", " +
            "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse actual = subject.run(tokenInput);
        assertThat(actual).isNotNull();
        assertThat(actual.getAccessToken()).isNotNull();
        assertThat(actual.getExpiresIn()).isEqualTo(3600);
        assertThat(actual.getTokenType()).isEqualTo(TokenType.BEARER.toString().toLowerCase());

    }

    @Test
    @Transactional
    public void testRunLoginClientFails() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \"" + plainTextAuthCode + "\", " +
                "\"redirect_uri\": \"" + authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword("wrong-password");

        UnauthorizedException expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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
        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getCode());
        assertThat(expected.getMessage()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getMessage());
        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    public void testMissingGrantTypeExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        // payload with out grant type.
        StringReader sr = new StringReader(
            "{\"code\": \""+ plainTextAuthCode + "\", " +
            "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        BadRequestException expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.MISSING_KEY.getCode());
        assertThat(((MissingKeyException) expected.getDomainCause()).getKey()).isEqualTo("grant_type");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("grant_type is a required field");
        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    public void testUnsupportedGrantTypeExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        String grantType = "unknown_grant_type";
        
        StringReader sr = new StringReader(
                "{\"code\": \""+ plainTextAuthCode + "\", " +
                "\"grant_type\": \"" + grantType + "\","+
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        BadRequestException expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.GRANT_TYPE_INVALID.getCode());
        assertThat(((InvalidValueException) expected.getDomainCause()).getKey()).isEqualTo("grant_type");
        assertThat(expected.getError()).isEqualTo("unsupported_grant_type");
        assertThat(expected.getDescription()).isEqualTo(grantType + " is not supported");
        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    public void testMissingCodeExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);


        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        BadRequestException expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.MISSING_KEY.getCode());
        assertThat(((MissingKeyException) expected.getDomainCause()).getKey()).isEqualTo("code");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("code is a required field");
        assertThat(actual).isNull();
    }
    
    @Test
    @Transactional
    public void testMissingRedirectUriExpectAuthorizationCodeNotFound() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"code\": \""+ plainTextAuthCode + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        AuthorizationCodeNotFound expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_grant");
        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    public void testIsRevokedExpectAuthorizationCodeNotFound() throws URISyntaxException, DuplicateRecordException {
        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, true, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \"" + plainTextAuthCode + "\", " +
                "\"redirect_uri\": \"" + authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        AuthorizationCodeNotFound expected = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.AUTH_CODE_NOT_FOUND.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_grant");
        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    public void testRedirectUriIsNotHttpsExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ FixtureFactory.REDIRECT_URI + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        BadRequestException expected = null;
        TokenResponse actual = null;

        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_INVALID.getCode());
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is invalid");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }

    @Test
    @Transactional
    public void testRedirectUriIsNotValidExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \"foo\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        BadRequestException expected = null;
        TokenResponse actual = null;

        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_INVALID.getCode());
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is invalid");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }

    @Test
    @Transactional
    public void testGrantTypeRepeatedExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse actual = null;
        BadRequestException expected = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getDescription()).isEqualTo("grant_type is repeated");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    public void testCodeRepeatedExpectBadRequestException() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse actual = null;
        BadRequestException expected = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getDescription()).isEqualTo("code is repeated");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    public void testRedirectUriRepeatedExpectBadRequest() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"," +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse actual = null;
        BadRequestException expected = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is repeated");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    public void testHasClientIdExpectBadRequest() throws URISyntaxException, DuplicateRecordException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"," +
                "\"client_id\": \"42415ecb-857d-4ff3-9223-f1c133b06205\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        TokenResponse actual = null;
        BadRequestException expected = null;
        try {
            actual = subject.run(tokenInput);
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

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.UNKNOWN_KEY.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("client_id is a unknown key");
        assertThat(expected.getDomainCause()).isInstanceOf(UnknownKeyException.class);
    }

    /**
     * Cannot run in a transaction because the transaction fails when the
     * unique key constraint is observed.
     *
     * @throws DuplicateRecordException
     * @throws URISyntaxException
     */
    @Test
    public void runExpectCompromisedCodeException() throws DuplicateRecordException, URISyntaxException {

        String plainTextAuthCode = randomString.run();
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false, plainTextAuthCode);
        Token token = FixtureFactory.makeToken(authCode.getUuid());
        tokenRepository.insert(token);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ plainTextAuthCode + "\", " +
                "\"redirect_uri\": \""+ authCode.getAccessRequest().getRedirectURI().get().toString() + "\"}"
        );
        BufferedReader json = new BufferedReader(sr);

        TokenInput tokenInput = new TokenInput();
        tokenInput.setPayload(json);
        tokenInput.setClientUUID(authCode.getAccessRequest().getClientUUID().toString());
        tokenInput.setClientPassword(FixtureFactory.PLAIN_TEXT_PASSWORD);

        CompromisedCodeException exception = null;
        TokenResponse actual = null;
        try {
            actual = subject.run(tokenInput);
        } catch (UnauthorizedException e) {
            fail("actual UnauthorizedException, expected CompromisedCodeException");
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("actual AuthorizationCodeNotFound, expected CompromisedCodeException");
        } catch (BadRequestException e) {
            fail("actual BadRequestException expected CompromisedCodeException");
        } catch (CompromisedCodeException e) {
            exception = e;
        }

        assertThat(actual).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo(ErrorCode.COMPROMISED_AUTH_CODE.getCode());
        assertThat(exception.getError()).isEqualTo("invalid_grant");
        assertThat(exception.getDomainCause()).isInstanceOf(DuplicateRecordException.class);

    }
}