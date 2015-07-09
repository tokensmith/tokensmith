package org.rootservices.authorization.grant.code.protocol.token;

import helper.fixture.FixtureFactory;
import helper.fixture.persistence.LoadConfidentialClientTokenReady;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rootservices.authorization.authenticate.LoginConfidentialClient;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.exception.BaseInformException;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;
import org.rootservices.authorization.grant.code.protocol.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidPayloadException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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

        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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

        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
    public void testMissingRedirectUriExpectAuthorizationCodeNotFound() throws RecordNotFoundException, InvalidValueException, InvalidPayloadException, MissingKeyException, DuplicateKeyException, URISyntaxException, UnauthorizedException {
        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
        assertThat(actual).isNull();
    }

    @Test
    public void testRedirectUriIsNotHttpsExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }

    @Test
    public void testRedirectUriIsNotValidExpectBadRequestException() throws URISyntaxException{

        AuthCode authCode = loadConfidentialClientTokenReady.run(true);

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
        assertThat(expected.getDomainCause()).isInstanceOf(InvalidValueException.class);
    }
}
