package net.tokensmith.authorization.oauth2.grant.refresh.factory;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.refresh.entity.TokenInputRefreshGrant;
import net.tokensmith.authorization.oauth2.grant.token.exception.InvalidValueException;
import net.tokensmith.authorization.oauth2.grant.token.exception.MissingKeyException;
import net.tokensmith.authorization.oauth2.grant.token.exception.UnknownKeyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 10/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class TokenInputRefreshGrantFactoryTest {
    private static String GRANT_TYPE = "grant_type";
    private static String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Autowired
    private TokenInputRefreshGrantFactory subject;

    public Map<String, String> makeRequest() {
        Map<String, String> request = new HashMap<>();
        request.put(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        request.put(subject.REFRESH_TOKEN, "refresh-token");
        request.put(subject.SCOPE, "email address");

        return request;
    }

    @Test
    public void runWithManyScopesShouldBeOk() throws Exception {
        Map<String, String> request = makeRequest();

        TokenInputRefreshGrant actual = subject.run(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(request.get(subject.REFRESH_TOKEN)));
        assertThat(actual.getScopes(), is(notNullValue()));
        assertThat(actual.getScopes().size(), is(2));
        assertThat(actual.getScopes().get(0), is("email"));
        assertThat(actual.getScopes().get(1), is("address"));
    }

    @Test
    public void runWithOneScopeShouldBeOk() throws Exception {
        Map<String, String> request = makeRequest();
        request.put(subject.SCOPE, "email");

        TokenInputRefreshGrant actual = subject.run(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(request.get(subject.REFRESH_TOKEN)));
        assertThat(actual.getScopes(), is(notNullValue()));
        assertThat(actual.getScopes().size(), is(1));
        assertThat(actual.getScopes().get(0), is("email"));
    }

    @Test
    public void runWithScopeValueEmptyShouldThrowInvalidValueException() throws Exception {
        Map<String, String> request = makeRequest();
        request.put(subject.SCOPE, "");

        InvalidValueException actual = null;
        try {
            subject.run(request);
        } catch (InvalidValueException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.EMPTY_VALUE.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.EMPTY_VALUE.getCode()));
        assertThat(actual.getKey(), is(subject.SCOPE));
        assertThat(actual.getValue(), is(""));
    }

    @Test
    public void runWithRefreshTokenNotInRequestShouldThrowMissingKeyException() throws Exception {
        Map<String, String> request = makeRequest();
        request.remove(subject.REFRESH_TOKEN);

        MissingKeyException actual = null;
        try {
            subject.run(request);
        } catch (MissingKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("missing key " + subject.REFRESH_TOKEN));
        assertThat(actual.getKey(), is(subject.REFRESH_TOKEN));


    }

    @Test
    public void runWhenRequiredAndOptionalAndUnknownKeysShouldThrowUnknownKeyException() throws Exception {
        Map<String, String> request = makeRequest();
        request.put("foo", "foo");

        UnknownKeyException actual = null;
        try {
            subject.run(request);
        } catch (UnknownKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.UNKNOWN_KEY.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.UNKNOWN_KEY.getCode()));
        assertThat(actual.getKey(), is("foo"));
    }

    @Test
    public void runWhenRequiredKeysAndUnknownKeyShouldThrowUnknownKeyException() throws Exception {
        Map<String, String> request = makeRequest();
        request.remove("scope");
        request.put("foo", "foo");

        UnknownKeyException actual = null;
        try {
            subject.run(request);
        } catch (UnknownKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is(ErrorCode.UNKNOWN_KEY.getDescription()));
        assertThat(actual.getCode(), is(ErrorCode.UNKNOWN_KEY.getCode()));
        assertThat(actual.getKey(), is("foo"));
    }


}