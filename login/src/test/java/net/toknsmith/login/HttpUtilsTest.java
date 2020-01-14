package net.toknsmith.login;

import helper.Factory;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HttpUtilsTest {
    private static String BASE_URL = "http://localhost:%s/%s";
    private static Integer HTTP_PORT = 8089;
    private HttpUtils subject;

    @Before
    public void setUp() {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        LoginFactory loginFactory = new LoginFactory();
        loginFactory.setSecrets(secrets);

        subject = loginFactory.httpUtils();
    }

    @Test
    public void toStatusCodeWhen200ShouldBeOk() {
        Optional<StatusCode> actual = subject.toStatusCode(200);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(StatusCode.OK));
    }

    @Test
    public void toStatusCodeWhen400ShouldBeBadRequest() {
        Optional<StatusCode> actual = subject.toStatusCode(400);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(StatusCode.BAD_REQUEST));
    }

    @Test
    public void toStatusCodeWhenUnknownShouldBeEmpty() {
        Optional<StatusCode> actual = subject.toStatusCode(599);

        assertThat(actual.isEmpty(), is(true));
    }
}