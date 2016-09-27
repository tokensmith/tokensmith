package org.rootservices.authorization.oauth2.grant.password.factory;




import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.foo.validator.TokenPayloadValidator;
import org.rootservices.authorization.oauth2.grant.password.entity.TokenInputPasswordGrant;
import org.rootservices.authorization.oauth2.grant.foo.exception.UnknownKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tommackenzie on 9/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class TokenInputPasswordGrantFactoryTest {
    private static String GRANT_TYPE = "grant_type";
    private static String GRANT_TYPE_PASSWORD = "password";
    @Autowired
    private TokenInputPasswordGrantFactory subject;

    public Map<String, String> makeRequest() {
        Map<String, String> request = new HashMap<>();
        request.put(GRANT_TYPE, GRANT_TYPE_PASSWORD);
        request.put(subject.USER_NAME, "test@rootservices.org");
        request.put(subject.PASSWORD, "password");
        request.put(subject.SCOPE, "email address");

        return request;
    }

    @Test
    public void runWithManyScopesShouldBeOk() throws Exception {
        Map<String, String> request = makeRequest();

        TokenInputPasswordGrant actual = subject.run(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUserName(), is(request.get(subject.USER_NAME)));
        assertThat(actual.getPassword(), is(request.get(subject.PASSWORD)));
        assertThat(actual.getScopes(), is(notNullValue()));
        assertThat(actual.getScopes().size(), is(2));
        assertThat(actual.getScopes().get(0), is("email"));
        assertThat(actual.getScopes().get(1), is("address"));
    }

    @Test
    public void runWithOneScopeShouldBeOk() throws Exception {
        Map<String, String> request = makeRequest();
        request.put(subject.SCOPE, "email");

        TokenInputPasswordGrant actual = subject.run(request);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getUserName(), is(request.get(subject.USER_NAME)));
        assertThat(actual.getPassword(), is(request.get(subject.PASSWORD)));
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
    public void runWithUserNameNotInRequestShouldThrowMissingKeyException() throws Exception {
        Map<String, String> request = makeRequest();
        request.remove(subject.USER_NAME);

        MissingKeyException actual = null;
        try {
            subject.run(request);
        } catch (MissingKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("missing key " + subject.USER_NAME));
        assertThat(actual.getKey(), is(subject.USER_NAME));


    }

    @Test
    public void runWithPasswordNotInRequestShouldThrowMissingKeyException() throws Exception {
        Map<String, String> request = makeRequest();
        request.remove(subject.PASSWORD);

        MissingKeyException actual = null;
        try {
            subject.run(request);
        } catch (MissingKeyException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("missing key " + subject.PASSWORD));
        assertThat(actual.getKey(), is(subject.PASSWORD));
    }


    @Test
    public void runWithTooManyKeysShouldThrowUnknownKeyException() throws Exception {
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

}