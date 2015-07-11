package org.rootservices.authorization.grant.code.protocol.token;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.exception.BaseInformException;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.*;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
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
@Transactional
public class RequestTokenImplTest {

    @Autowired
    private LoadConfidentialClientTokenReady loadConfidentialClientTokenReady;

    @Autowired
    private RequestToken subject;

    @Test
    public void testRun() throws Exception {

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
    public void testRunLoginClientFails() throws URISyntaxException, UnauthorizedException, RecordNotFoundException, InvalidValueException, InvalidPayloadException, MissingKeyException, DuplicateKeyException {

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \"" + FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected UnauthorizedException");
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getCode());
        assertThat(expected.getMessage()).isEqualTo(ErrorCode.PASSWORD_MISMATCH.getMessage());
        assertThat(actual).isNull();
    }

    @Test
    public void testMissingGrantTypeExpectBadRequestException() throws RecordNotFoundException, InvalidValueException, InvalidPayloadException, MissingKeyException, DuplicateKeyException, URISyntaxException, UnauthorizedException {
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        // payload with out grant type.
        StringReader sr = new StringReader(
            "{\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        }
        catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected BadRequestException");
        }

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.MISSING_KEY.getCode());
        assertThat(((MissingKeyException) expected.getDomainCause()).getKey()).isEqualTo("grant_type");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("grant_type is a required field");
        assertThat(actual).isNull();
    }


    @Test
    public void testMissingCodeExpectBadRequestException() throws RecordNotFoundException, InvalidValueException, InvalidPayloadException, MissingKeyException, DuplicateKeyException, URISyntaxException, UnauthorizedException {
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

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
        }
        catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected BadRequestException");
        }

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.MISSING_KEY.getCode());
        assertThat(((MissingKeyException) expected.getDomainCause()).getKey()).isEqualTo("code");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("code is a required field");
        assertThat(actual).isNull();
    }
    
    @Test
    public void testMissingRedirectUriExpectAuthorizationCodeNotFound() throws URISyntaxException {
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\"}"
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
        } catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected AuthorizationCodeNotFound");
        }

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_grant");
        assertThat(actual).isNull();
    }

    @Test
    public void testIsRevokedExpectAuthorizationCodeNotFound() throws URISyntaxException {
        AuthCode authCode = loadConfidentialClientTokenReady.run(true, true);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \"" + FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (BaseInformException e) {
            fail("BaseInformException was thrown. Expected AuthorizationCodeNotFound");
        }

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.AUTH_CODE_NOT_FOUND.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_grant");
        assertThat(actual).isNull();
    }


    @Test
    public void testRedirectUriIsNotHttpsExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_INVALID.getCode());
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is invalid");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }

    @Test
    public void testRedirectUriIsNotValidExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.REDIRECT_URI_INVALID.getCode());
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is invalid");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }

    @Test
    public void testGrantTypeRepeatedExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getDescription()).isEqualTo("grant_type is repeated");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void testCodeRepeatedExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getDescription()).isEqualTo("code is repeated");
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void testRedirectUriRepeatedExpectBadRequest() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("redirect_uri is repeated");
        assertThat(expected.getDomainCause()).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void testHasClientIdExpectBadRequest() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true, false);

        StringReader sr = new StringReader(
                "{\"grant_type\": \"authorization_code\", " +
                "\"code\": \""+ FixtureFactory.PLAIN_TEXT_AUTHORIZATION_CODE + "\", " +
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
        } catch (AuthorizationCodeNotFound authorizationCodeNotFound) {
            fail("BadRequestException expected");
        } catch (BadRequestException e) {
            expected = e;
        }

        assertThat(actual).isNull();
        assertThat(expected.getCode()).isEqualTo(ErrorCode.UNKNOWN_KEY.getCode());
        assertThat(expected.getError()).isEqualTo("invalid_request");
        assertThat(expected.getDescription()).isEqualTo("client_id is a unknown key");
        assertThat(expected.getDomainCause()).isInstanceOf(UnknownKeyException.class);
    }
}
