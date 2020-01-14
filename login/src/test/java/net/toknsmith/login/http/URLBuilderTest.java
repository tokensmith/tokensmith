package net.toknsmith.login.http;

import net.toknsmith.login.exception.URLException;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class URLBuilderTest {
    private URLBuilder subject;

    @Before
    public void setUp() {
        subject = new URLBuilder();
    }

    @Test
    public void buildWhenAuthorizationParamsShouldBeOK() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("profile");

        URL actual = subject
                .baseUrl(new URI("http://sso.tokensmith.net"))
                .param("client_id", "1234")
                .param("response_type", "code")
                .param("redirect_uri", "http://tokensmith.net/account")
                .paramsWhiteSpaceDelimitted("scope", scopes)
                .param("state", "some-state")
                .param("nonce", "some-nonce")
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.toString(), is("http://sso.tokensmith.net?client_id=1234&response_type=code&redirect_uri=http%3A%2F%2Ftokensmith.net%2Faccount&scope=openid+profile&state=some-state&nonce=some-nonce"));
    }

    @Test
    public void buildWhenAuthorizationParamsMultipleCallsToWhiteSpaceDelimitedShouldBeOK() throws Exception {
        List<String> scopes1 = new ArrayList<>();
        scopes1.add("openid");
        scopes1.add("profile");

        List<String> scopes2 = new ArrayList<>();
        scopes2.add("email");

        URL actual = subject
                .baseUrl(new URI("http://sso.tokensmith.net"))
                .param("client_id", "1234")
                .param("response_type", "code")
                .param("redirect_uri", "http://tokensmith.net/account")
                .paramsWhiteSpaceDelimitted("scope", scopes1)
                .paramsWhiteSpaceDelimitted("scope", scopes2)
                .param("state", "some-state")
                .param("nonce", "some-nonce")
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.toString(), is("http://sso.tokensmith.net?client_id=1234&response_type=code&redirect_uri=http%3A%2F%2Ftokensmith.net%2Faccount&scope=openid+profile+email&state=some-state&nonce=some-nonce"));
    }
}