package net.toknsmith.login.endpoint;

import helper.Factory;
import net.toknsmith.login.HttpUtils;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.exception.URLException;
import net.toknsmith.login.exception.http.api.ClientException;
import net.toknsmith.login.exception.http.api.ServerException;
import net.toknsmith.login.http.StatusCode;
import net.toknsmith.login.translator.JwtRSAPublicKeyTranslator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class KeyEndpointTest {
    private KeyEndpoint subject;

    @Mock
    private JwtRSAPublicKeyTranslator mockJwtRSAPublicKeyTranslator;
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private HttpUtils mockHttpUtils;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String publicKeyURL = "http://localhost:8009/api/public/v1/jwk/rsa/%s";

        subject = new KeyEndpoint(
                mockJwtRSAPublicKeyTranslator,
                mockHttpClient,
                mockHttpUtils,
                publicKeyURL
        );
    }

    @Test
    public void makeKeyEndpointShouldBeOk() throws Exception {
        URI actual = subject.makeKeyEndpoint("1234");
        assertThat(actual, is(notNullValue()));
        assertThat(actual.toString(), is("http://localhost:8009/api/public/v1/jwk/rsa/1234"));
    }

    @Test
    public void makeKeyEndpointShouldThrowURLException() {
        URLException actual = null;
        try {
            subject.makeKeyEndpoint("}{}{}{}}{");
        } catch (URLException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void handleNotOkWhenBadRequest() throws Exception {
        HttpResponse<InputStream> response = Factory.makeFakeResponseForApiBadRequest();
        LoginFactory loginFactory = new LoginFactory();
        HttpUtils httpUtils = loginFactory.httpUtils();
        InputStream body = httpUtils.processResponse(response);

        ClientException actual = null;
        try {
            subject.handleNotOk(response, body);
        } catch (ClientException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));
        assertThat(actual.getClientError(), is(notNullValue()));
    }

    @Test
    public void handleNotOkWhenServerError() throws Exception {
        HttpResponse<InputStream> response = Factory.makeFakeResponseForApiServerError();
        LoginFactory loginFactory = new LoginFactory();
        HttpUtils httpUtils = loginFactory.httpUtils();
        InputStream body = httpUtils.processResponse(response);

        ServerException actual = null;
        try {
            subject.handleNotOk(response, body);
        } catch (ServerException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));
        assertThat(actual.getServerError(), is(notNullValue()));
    }
}