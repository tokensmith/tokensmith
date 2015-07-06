package org.rootservices.authorization.grant.code.protocol.token.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by tommackenzie on 7/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-auth-test.xml")
public class IsTokenRequestValidImplTest {

    @Autowired
    private IsTokenRequestValid subject;

    public TokenRequest makeTokenRequest() throws URISyntaxException {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setCode("test-code");
        tokenRequest.setGrantType("authorization_code");
        tokenRequest.setRedirectUri(Optional.of(new URI("https://rootservices.com/contine")));

        return tokenRequest;
    }

    @Test
    public void run() throws URISyntaxException, InvalidValueException, MissingKeyException {
        TokenRequest tokenRequest = makeTokenRequest();
        boolean actual = subject.run(tokenRequest);
        assertThat(actual).isTrue();
    }

    @Test
    public void runRedirectUriIsEmptyExpectTrue() throws URISyntaxException, InvalidValueException, MissingKeyException {
        TokenRequest tokenRequest = makeTokenRequest();
        tokenRequest.setRedirectUri(Optional.empty());

        boolean actual = subject.run(tokenRequest);
        assertThat(actual).isTrue();
    }

    @Test
    public void grantTypeIsNullExpectMissingException() throws URISyntaxException {
        TokenRequest tokenRequest = makeTokenRequest();
        tokenRequest.setGrantType(null);

        MissingKeyException expected = null;
        Boolean actual = null;
        try {
            actual = subject.run(tokenRequest);
            fail("MissingKeyException expected.");
        } catch (InvalidValueException e) {
            fail("MissingKeyException expected.");
        } catch (MissingKeyException e) {
            expected = e;
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("grant_type");
        assertThat(actual).isNull();
    }

    @Test
    public void grantTypeIsNotAuthorizationCodeExpectInvalidValueException() throws URISyntaxException {
        TokenRequest tokenRequest = makeTokenRequest();
        tokenRequest.setGrantType("invalid");

        InvalidValueException expected = null;
        Boolean actual = null;
        try {
            actual = subject.run(tokenRequest);
            fail("InvalidValueException expected.");
        } catch (InvalidValueException e) {
            expected = e;
        } catch (MissingKeyException e) {
            fail("InvalidValueException expected.");
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("grant_type");
        assertThat(expected.getValue()).isEqualTo("invalid");
        assertThat(actual).isNull();
    }

    @Test
    public void codeIsNull() throws URISyntaxException {
        TokenRequest tokenRequest = makeTokenRequest();
        tokenRequest.setCode(null);

        MissingKeyException expected = null;
        Boolean actual = null;
        try {
            actual = subject.run(tokenRequest);
            fail("MissingKeyException expected.");
        } catch (InvalidValueException e) {
            fail("MissingKeyException expected.");
        } catch (MissingKeyException e) {
            expected = e;
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("code");
        assertThat(actual).isNull();
    }

    @Test
    public void redirectUriIsInvalidExpectInvalidValueException() throws URISyntaxException {
        TokenRequest tokenRequest = makeTokenRequest();
        tokenRequest.setRedirectUri(Optional.of(new URI("http://www.rootservices.org/continue")));

        InvalidValueException expected = null;
        Boolean actual = null;
        try {
            actual = subject.run(tokenRequest);
            fail("InvalidValueException expected.");
        } catch (InvalidValueException e) {
            expected = e;
        } catch (MissingKeyException e) {
            fail("InvalidValueException expected.");
        }
        assertThat(expected).isNotNull();
        assertThat(expected.getKey()).isEqualTo("redirect_uri");
        assertThat(expected.getValue()).isEqualTo("http://www.rootservices.org/continue");
        assertThat(actual).isNull();
    }
}