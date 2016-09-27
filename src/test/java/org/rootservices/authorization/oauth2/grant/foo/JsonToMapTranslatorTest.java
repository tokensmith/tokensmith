package org.rootservices.authorization.oauth2.grant.foo;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.foo.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.foo.exception.InvalidPayloadException;
import org.rootservices.authorization.oauth2.grant.foo.translator.JsonToMapTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 9/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class JsonToMapTranslatorTest {
    @Autowired
    private JsonToMapTranslator subject;

    @Test
    public void toShouldTranslate() throws Exception {
        StringReader sr = new StringReader("{\"grant_type\": \"authorization_code\", \"code\": \"test-code\", \"redirect_uri\": \"https://rootservices.org/continue\"}");
        BufferedReader json = new BufferedReader(sr);

        Map<String, String> actual = subject.to(json);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.get("grant_type"), is("authorization_code"));
        assertThat(actual.get("code"), is("test-code"));
        assertThat(actual.get("redirect_uri"), is("https://rootservices.org/continue"));
    }

    @Test
    public void toWithDuplicateGrantTypeExpectDuplicateKeyException() throws Exception {

        StringReader sr = new StringReader("{\"grant_type\": \"authorization_code\", \"grant_type\": \"duplicate_authorization_code\", \"code\": \"test-code\", \"redirect_uri\": \"https://rootservices.org/continue\"}");
        BufferedReader json = new BufferedReader(sr);

        DuplicateKeyException expected = null;
        try {
            subject.to(json);
            fail("DuplicateKeyException was expected.");
        } catch (DuplicateKeyException e) {
            expected = e;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getKey(), is("grant_type"));
        assertThat(expected.getDomainCause(), instanceOf(JsonParseException.class));
        assertThat(expected.getCode(), is(ErrorCode.DUPLICATE_KEY.getCode()));
        assertThat(expected.getMessage(), is(ErrorCode.DUPLICATE_KEY.getDescription()));

    }

    @Test
    public void toWhenIsEmptyExpectInvalidPayloadException() throws Exception {

        StringReader sr = new StringReader("");
        BufferedReader json = new BufferedReader(sr);

        InvalidPayloadException expected = null;

        try {
            subject.to(json);
            fail("InvalidPayloadException was expected.");
        } catch (InvalidPayloadException e) {
            expected = e;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getDomainCause(), instanceOf(IOException.class));
        assertThat(expected.getCode(), is(ErrorCode.INVALID_PAYLOAD.getCode()));
        assertThat(expected.getMessage(), is(ErrorCode.INVALID_PAYLOAD.getDescription()));
    }

    @Test
    public void toWhenBadJsonExpectInvalidPayloadException() throws Exception {

        StringReader sr = new StringReader("redirect");
        BufferedReader json = new BufferedReader(sr);

        InvalidPayloadException expected = null;

        try {
            subject.to(json);
            fail("InvalidPayloadException was expected.");
        } catch (InvalidPayloadException e) {
            expected = e;
        }

        assertThat(expected, is(notNullValue()));
        assertThat(expected.getDomainCause(), instanceOf(IOException.class));
        assertThat(expected.getCode(), is(ErrorCode.INVALID_PAYLOAD.getCode()));
        assertThat(expected.getMessage(), is(ErrorCode.INVALID_PAYLOAD.getDescription()));
    }
}