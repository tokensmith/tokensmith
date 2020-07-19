package net.tokensmith.authorization.oauth2.grant.token.validator;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.token.exception.InvalidValueException;
import net.tokensmith.authorization.oauth2.grant.token.exception.MissingKeyException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 9/21/16.
 */
public class TokenPayloadValidatorTest {
    private TokenPayloadValidator subject;

    @Before
    public void setUp() {
        subject = new TokenPayloadValidator();
    }

    @Test
    public void requiredShouldReturnInput() throws Exception {
        String actual = subject.required("tom", "username");
        assertThat(actual, is("tom"));
    }

    @Test
    public void requiredWhenInputIsNullShouldThrowMissingKeyException() throws Exception {
        MissingKeyException actual = null;
        try {
            subject.required(null, "username");
        } catch (MissingKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("missing key username"));
        assertThat(actual.getKey(), is("username"));
    }

    @Test
    public void requiredWhenInputIsEmptyShouldThrowMissingKeyException() throws Exception {
        MissingKeyException actual = null;
        try {
            subject.required("", "username");
        } catch (MissingKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("missing key username"));
        assertThat(actual.getKey(), is("username"));

    }

    @Test
    public void optionalWhenInputIsNotEmptyShouldReturnInput() throws Exception {

        Optional<String> actual = subject.optional("profile", "scope");

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("profile"));
    }

    @Test
    public void optionalWhenInputNullShouldReturnInput() throws Exception {
        Optional<String> actual = subject.optional(null, "scope");

        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void optionalWhenInputIEmptyShouldThrowInvalidValueException() throws Exception {
        InvalidValueException actual = null;
        try {
            subject.optional("", "scope");
        } catch (InvalidValueException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.EMPTY_VALUE.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.EMPTY_VALUE.getCode()));
        assertThat(actual.getKey(), is("scope"));
        assertThat(actual.getValue(), is(""));
    }

    @Test
    public void getFirstUnknownKeyShouldReturnKey() throws Exception {
        List<String> knownKeys = Arrays.asList("username", "password");

        Map<String, String> payload = new HashMap<>();
        payload.put("username", "tom");
        payload.put("password", "password");
        payload.put("foo", "foo value");

        Optional<String> actual = subject.getFirstUnknownKey(payload, knownKeys);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("foo"));
    }

    @Test
    public void getFirstUnknownKeyWhenNotFoundShouldReturnEmptyOptional() throws Exception {
        List<String> knownKeys = Arrays.asList("username", "password");

        Map<String, String> payload = new HashMap<>();
        payload.put("username", "tom");
        payload.put("password", "password");

        Optional<String> actual = subject.getFirstUnknownKey(payload, knownKeys);

        assertThat(actual.isPresent(), is(false));
    }
}