package org.rootservices.authorization.grant.code.protocol.token;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.token.factory.JsonToTokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.JsonToTokenRequestImpl;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidPayloadException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;
import org.rootservices.authorization.grant.code.protocol.token.validator.IsTokenRequestValid;
import org.rootservices.authorization.grant.code.protocol.token.validator.IsTokenRequestValidImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 6/29/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class JsonToTokenRequestImplTest {

    @Autowired
    private JsonToTokenRequest subject;

    @Test
    public void run() throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException {

        StringReader sr = new StringReader("{\"grant_type\": \"authorization_code\", \"code\": \"test-code\", \"redirect_uri\": \"https://rootservices.org/continue\"}");
        BufferedReader json = new BufferedReader(sr);

        TokenRequest actual = subject.run(json);
        assertThat(actual).isNotNull();
        assertThat(actual.getGrantType()).isEqualTo("authorization_code");
        assertThat(actual.getCode()).isEqualTo("test-code");
        assertThat(actual.getRedirectUri().get().toString()).isEqualTo("https://rootservices.org/continue");
    }

    @Test
    public void runWithDuplicateGrantTypeExpectDuplicateKeyException() {

        StringReader sr = new StringReader("{\"grant_type\": \"authorization_code\", \"grant_type\": \"duplicate_authorization_code\", \"code\": \"test-code\", \"redirect_uri\": \"https://rootservices.org/continue\"}");
        BufferedReader json = new BufferedReader(sr);

        DuplicateKeyException expected = null;
        TokenRequest actual = null;
        try {
            actual = subject.run(json);
            fail("DuplicateKeyException was expected.");
        } catch (DuplicateKeyException e) {
            expected = e;
        } catch (InvalidPayloadException e) {
            fail("DuplicateKeyException was expected.");
        } catch (MissingKeyException e) {
            fail("DuplicateKeyException was expected.");
        } catch (InvalidValueException e) {
            fail("DuplicateKeyException was expected.");
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("grant_type");
        assertThat(expected.getDomainCause()).isInstanceOf(JsonParseException.class);
        assertThat(expected.getCode()).isEqualTo(ErrorCode.DUPLICATE_KEY.getCode());
        assertThat(expected.getMessage()).isEqualTo(ErrorCode.DUPLICATE_KEY.getMessage());
        assertThat(actual).isNull();
    }

    @Test
    public void runInvalidRedirectUriExpectInvalidValueException() {

        String invalidRedirectUri = "invalid-redirect-uri";
        StringReader sr = new StringReader(
            "{\"grant_type\": \"authorization_code\", " +
            "\"code\": \"test-code\", " +
            "\"redirect_uri\": \""+invalidRedirectUri+"\"}"
        );
        BufferedReader json = new BufferedReader(sr);


        InvalidValueException expected = null;
        TokenRequest actual = null;
        try {
            actual = subject.run(json);
        } catch (DuplicateKeyException e) {
            fail("InvalidValueException was expected.");
        } catch (InvalidPayloadException e) {
            fail("InvalidValueException was expected.");
        } catch (MissingKeyException e) {
            fail("InvalidValueException was expected.");
        } catch (InvalidValueException e) {
            expected = e;
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("redirect_uri");
        assertThat(expected.getValue()).isEqualTo(invalidRedirectUri);
        assertThat(actual).isNull();
    }

    @Test
    public void runJsonIsEmptyExpectInvalidPayloadException() {

        StringReader sr = new StringReader("");
        BufferedReader json = new BufferedReader(sr);

        InvalidPayloadException expected = null;
        TokenRequest actual = null;
        try {
            actual = subject.run(json);
            fail("InvalidPayloadException was expected.");
        } catch (DuplicateKeyException e) {
            fail("InvalidPayloadException was expected.");
        } catch (InvalidValueException e) {
            fail("InvalidPayloadException was expected.");
        } catch (MissingKeyException e) {
            fail("InvalidPayloadException was expected.");
        } catch (InvalidPayloadException e) {
            expected = e;
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getDomainCause()).isInstanceOf(IOException.class);
        assertThat(expected.getCode()).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(expected.getMessage()).isEqualTo(ErrorCode.INVALID_PAYLOAD.getMessage());
        assertThat(actual).isNull();
    }

    @Test
    public void runJsonIsEmptyContainerExpectMissingKeyException() {

        StringReader sr = new StringReader("{}");
        BufferedReader json = new BufferedReader(sr);

        MissingKeyException expected = null;
        TokenRequest actual = null;
        try {
            actual = subject.run(json);
        } catch (DuplicateKeyException e) {
            fail("MissingKeyException was expected");
        } catch (InvalidPayloadException e) {
            fail("MissingKeyException was expected");
        } catch (InvalidValueException e) {
            fail("MissingKeyException was expected");
        } catch (MissingKeyException e) {
            expected = e;
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("grant_type");
        assertThat(actual).isNull();
    }

}