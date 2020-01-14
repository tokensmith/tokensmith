package net.toknsmith.login.translator;

import helper.Factory;
import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.endpoint.entity.response.openid.TokenErrorResponse;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.http.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class TokenErrorResponseTranslatorTest {
    private ErrorResponseTranslator subject;
    private HttpUtils httpUtils;

    @Before
    public void setUp() {
        LoginFactory factory = new LoginFactory();
        subject = new ErrorResponseTranslator(factory.objectMapper());
        LoginFactory loginFactory = new LoginFactory();
        httpUtils = loginFactory.httpUtils();
    }

    @Test
    public void toWhenBadRequestShouldBeOk() throws Exception {
        HttpResponse<InputStream> response = Factory.makeFakeResponseBadRequest();
        InputStream body = httpUtils.processResponse(response);

        TokenErrorResponse actual = subject.to(response, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getError(), is(notNullValue()));
        assertThat(actual.getError(), is("invalid_request"));
        assertThat(actual.getDescription(), is(notNullValue()));
        assertThat(actual.getDescription().isPresent(), is(true));
    }

    @Test
    public void toWhenUnauthorizedShouldBeOk() throws Exception {
        HttpResponse<InputStream> response = Factory.makeFakeResponseUnAuthorized();
        InputStream body = httpUtils.processResponse(response);

        TokenErrorResponse actual = subject.to(response, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getError(), is(notNullValue()));
        assertThat(actual.getError(), is("invalid_client"));
        assertThat(actual.getDescription(), is(notNullValue()));
        assertThat(actual.getDescription().isPresent(), is(false));
    }

    @Test
    public void toWhenNotFoundShouldBeOk() throws Exception {
        HttpResponse<InputStream> response = Factory.makeFakeResponseNotFound();
        InputStream body = httpUtils.processResponse(response);

        TokenErrorResponse actual = subject.to(response, body);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getError(), is(notNullValue()));
        assertThat(actual.getError(), is("invalid_grant"));
        assertThat(actual.getDescription(), is(notNullValue()));
        assertThat(actual.getDescription().isPresent(), is(true));
    }

    @Test
    public void toWhenInvalidBodyShouldThrowTranslationException() throws Exception {
        Map<String, List<String>> headerMap = new HashMap<>();
        InputStream responseBody = new ByteArrayInputStream("unexpected-response-body".getBytes(StandardCharsets.UTF_8));
        HttpResponse<InputStream> response = Factory.makeFakeResponse(headerMap, responseBody, StatusCode.BAD_REQUEST);

        InputStream body = httpUtils.processResponse(response);

        TranslateException actual = null;
        try {
            subject.to(response, body);
        } catch (TranslateException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual, instanceOf(TranslateException.class));
    }
}