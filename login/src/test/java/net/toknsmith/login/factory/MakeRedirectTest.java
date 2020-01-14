package net.toknsmith.login.factory;

import helper.Factory;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.model.Redirect;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class MakeRedirectTest {
    private static String BASE_URL = "http://localhost:%s/%s";
    private static Integer HTTP_PORT = 8089;
    private MakeRedirect subject;

    @Before
    public void setUp() {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        LoginFactory loginFactory = new LoginFactory();
        loginFactory.setSecrets(secrets);
        subject = loginFactory.makeRedirect();
    }

    @Test
    public void makeRedirect() throws Exception {
        String state = "some-state";
        String redirect = "https://tokensmith.net/account";
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("profile");

        Redirect actual = subject.makeRedirect(state, redirect, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getLocation(), is(notNullValue()));
        assertThat(actual.getState(), is(notNullValue()));
        assertThat(actual.getState(), is(state));
        assertThat(actual.getNonce(), is(notNullValue()));
    }
}