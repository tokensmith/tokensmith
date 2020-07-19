package integration.net.tokensmith.login;


import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import helper.Factory;
import helper.wiremock.JettyHttpServerFactory;
import net.toknsmith.login.Login;
import net.toknsmith.login.config.LoginFactory;
import net.toknsmith.login.endpoint.entity.response.api.key.RSAPublicKey;
import net.toknsmith.login.endpoint.entity.response.openid.claim.User;
import net.toknsmith.login.exception.CommException;
import net.toknsmith.login.exception.IdTokenException;
import net.toknsmith.login.exception.TranslateException;
import net.toknsmith.login.exception.http.openid.ErrorResponseException;
import net.toknsmith.login.model.Redirect;
import net.toknsmith.login.model.UserWithTokens;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class TokenSmithLoginTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSmithLoginTest.class);

    private static Integer HTTP_PORT = 8089;
    private static Integer HTTPS_PORT = 8090;
    private static String BASE_URL = "http://localhost:%s/%s";

    @Rule
    public WireMockRule IdServer = new WireMockRule(
        new WireMockConfiguration()
            .port(HTTP_PORT)
            .httpsPort(HTTPS_PORT)
            .httpServerFactory(new JettyHttpServerFactory())
            .notifier(new Slf4jNotifier(true))
    );

    private static LoginFactory loginFactory = new LoginFactory();
    private static String correlationId;
    private static String version;


    @BeforeClass
    public static void beforeClass() {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        loginFactory.setSecrets(secrets);

        // set the correlation id.
        correlationId = loginFactory.httpUtils().getCorrelationId();
        version = "0.0.1-SNAPSHOT"; // gross.
    }

    public void stubToken(WireMockRule toStub, Map<String, String> secrets, String form, com.github.tomakehurst.wiremock.http.HttpHeaders responseHeaders, byte[] response) {
        toStub.stubFor(
            post(
                urlEqualTo("/api/public/v1/token")
            )
            .withBasicAuth(secrets.get("CLIENT_USER_NAME"), secrets.get("CLIENT_PASSWORD"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
            .withHeader("Accept", equalTo("application/json;charset=UTF-8"))
            .withHeader("Accept-Encoding", equalTo("gzip"))
            .withHeader("X-Correlation-ID", equalTo(correlationId))
            .withHeader("X-Login-SDK", equalTo("login-sdk-java"))
            .withHeader("X-Login-SDK-Version", equalTo(version))
            .withRequestBody(equalTo(form))
            .willReturn(
                aResponse()
                .withStatus(200)
                .withHeaders(responseHeaders)
                .withBody(response)
            )
        );
    }

    public void stubKey(WireMockRule toStub, String keyId, com.github.tomakehurst.wiremock.http.HttpHeaders responseHeaders, byte[] response) {
        String keyUri = String.format("/api/public/v1/jwk/rsa/%s", keyId);
        toStub.stubFor(
            get(
                urlEqualTo(keyUri)
            )
            .withHeader("Content-Type", equalTo("application/json;charset=UTF-8"))
            .withHeader("Accept", equalTo("application/json;charset=UTF-8"))
            .withHeader("Accept-Encoding", equalTo("gzip"))
            .withHeader("X-Correlation-ID", equalTo(correlationId))
            .withHeader("X-Login-SDK", equalTo("login-sdk-java"))
            .withHeader("X-Login-SDK-Version", equalTo(version))
            .willReturn(
                aResponse()
                .withStatus(200)
                .withHeaders(responseHeaders)
                .withBody(response)
            )
        );
    }

    @Test
    public void passwordGrantShouldReturnUser() throws Exception {

        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        Login subject = loginFactory.tokenSmithLogin();
        String response = loginFactory.objectMapper().writeValueAsString(Factory.okIdToken());

        List<String> scopes = Factory.scopes();
        String form = "password=foo&grant_type=password&scope=profile openid&username=obi-wan@tokensmith.net";

        stubToken(IdServer, secrets, form, Factory.okTokenResponseHeaders(), response.getBytes());

        RSAPublicKey key = Factory.rsaPublicKey();
        String keyResponse = loginFactory.objectMapper().writeValueAsString(key);
        stubKey(IdServer, key.getKeyId().toString(), Factory.okTokenResponseHeaders(), keyResponse.getBytes());

        UserWithTokens actual = null;
        try {
            // TODO: validate nonce.
            actual = subject.withPassword("obi-wan@tokensmith.net", "foo", scopes);
        } catch (CommException | ErrorResponseException | TranslateException e) {

            List<NearMiss> misses = IdServer.findNearMissesForAllUnmatchedRequests();
            LOGGER.error("near misses: {}", misses.toString());

            String message = String.format("exception encountered %s", e.getMessage());
            fail(message);
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getUser(), is(notNullValue()));
    }


    @Test
    public void refreshGrantTypeShouldReturnUser() throws Exception {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        Login subject = loginFactory.tokenSmithLogin();
        String response = loginFactory.objectMapper().writeValueAsString(Factory.okIdToken());

        String form = "refresh_token=abc123&grant_type=refresh_token";

        stubToken(IdServer, secrets, form, Factory.okTokenResponseHeaders(), response.getBytes());

        RSAPublicKey key = Factory.rsaPublicKey();
        String keyResponse = loginFactory.objectMapper().writeValueAsString(key);
        stubKey(IdServer, key.getKeyId().toString(), Factory.okTokenResponseHeaders(), keyResponse.getBytes());

        UserWithTokens actual = null;
        try {
            // TODO: validate nonce.
            actual = subject.withRefreshToken("abc123");
        } catch (CommException | ErrorResponseException | TranslateException e) {
            List<NearMiss> misses = IdServer.findNearMissesForAllUnmatchedRequests();
            LOGGER.error("near misses: {}", misses.toString());

            String message = String.format("exception encountered %s", e.getMessage());
            fail(message);
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getUser(), is(notNullValue()));
    }

    @Test
    public void codeGrantShouldReturnUser() throws Exception {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        Login subject = loginFactory.tokenSmithLogin();
        String response = loginFactory.objectMapper().writeValueAsString(Factory.okIdToken());

        String redirectUri = "http://tokensmith.net/welcome";
        String form = "code=foo&grant_type=authorization_code&redirect_uri=" + redirectUri;

        stubToken(IdServer, secrets, form, Factory.okTokenResponseHeaders(), response.getBytes());

        RSAPublicKey key = Factory.rsaPublicKey();
        String keyResponse = loginFactory.objectMapper().writeValueAsString(key);
        stubKey(IdServer, key.getKeyId().toString(), Factory.okTokenResponseHeaders(), keyResponse.getBytes());

        UserWithTokens actual = null;
        try {
            actual = subject.withCode("foo", "nonce-123", redirectUri);
        } catch (CommException | ErrorResponseException | TranslateException e) {
            List<NearMiss> misses = IdServer.findNearMissesForAllUnmatchedRequests();
            LOGGER.error("near misses: {}", misses.toString());

            String message = String.format("exception encountered %s", e.getMessage());
            fail(message);
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getUser(), is(notNullValue()));
    }

    @Test
    public void codeGrantNonceDoesNotMatchShouldThrowIdTokenException() throws Exception {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        Login subject = loginFactory.tokenSmithLogin();
        String response = loginFactory.objectMapper().writeValueAsString(Factory.okIdToken());

        String redirectUri = "http://tokensmith.net/welcome";
        String form = "code=foo&grant_type=authorization_code&redirect_uri=" + redirectUri;

        stubToken(IdServer, secrets, form, Factory.okTokenResponseHeaders(), response.getBytes());

        RSAPublicKey key = Factory.rsaPublicKey();
        String keyResponse = loginFactory.objectMapper().writeValueAsString(key);
        stubKey(IdServer, key.getKeyId().toString(), Factory.okTokenResponseHeaders(), keyResponse.getBytes());

        IdTokenException actual = null;
        try {
            subject.withCode("foo", "incorrect-nonce-123", redirectUri);
        } catch (IdTokenException e) {
            actual = e;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("Invalid nonce"));
        assertThat(actual.getCause(), is(nullValue()));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getTokenType(), is(notNullValue()));
        assertThat(actual.getExpiresIn(), is(notNullValue()));
        assertThat(actual.getUser(), is(notNullValue()));
    }

    @Test
    public void redirectShouldReturnRedirect() throws Exception {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        loginFactory.setSecrets(secrets);
        Login subject = loginFactory.tokenSmithLogin();

        String state = "state-123";
        String redirectUri = "http://tokensmith.net/welcome";
        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        Redirect actual = subject.authorizationEndpoint(state, redirectUri, scopes);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getState(), is(state));
        assertThat(actual.getNonce(), is(notNullValue()));

        String expectedRedirect = new StringBuilder()
                .append("http://localhost:8089/authorization?client_id=123456789&response_type=code&redirect_uri=http%3A%2F%2Ftokensmith.net%2Fwelcome&")
                .append("scope=profile&")
                .append("state=").append(state).append("&")
                .append("nonce=").append(actual.getNonce())
                .toString();

        assertThat(actual.getLocation().toString(), is(expectedRedirect));
    }

    @Test
    public void userInfoShouldReturnUser() throws Exception {
        Map<String, String> secrets = Factory.secrets(BASE_URL, HTTP_PORT);
        Login subject = loginFactory.tokenSmithLogin();

        IdServer.stubFor(
            get(
                urlEqualTo("/api/public/v1/userinfo")
            )
            .withHeader("Authorization", equalTo("Bearer foo"))
            .withHeader("Content-Type", equalTo("application/json;charset=UTF-8"))
            .withHeader("Accept", equalTo("application/jwt"))
            .withHeader("Accept-Encoding", equalTo("gzip"))
            .withHeader("X-Correlation-ID", equalTo(correlationId))
            .withHeader("X-Login-SDK", equalTo("login-sdk-java"))
            .withHeader("X-Login-SDK-Version", equalTo(version))
            .willReturn(
                aResponse()
                .withStatus(200)
                .withBody(Factory.okUserInfoResponseBody())
            )
        );

        RSAPublicKey key = Factory.rsaPublicKey();
        String keyResponse = loginFactory.objectMapper().writeValueAsString(key);
        stubKey(IdServer, key.getKeyId().toString(), Factory.okTokenResponseHeaders(), keyResponse.getBytes());

        User actual = null;
        try {
            actual = subject.userInfo("foo");
        } catch (CommException | ErrorResponseException | TranslateException | IdTokenException e) {
            List<NearMiss> misses = IdServer.findNearMissesForAllUnmatchedRequests();
            LOGGER.error("near misses: {}", misses.toString());

            String message = String.format("exception encountered %s", e.getMessage());
            fail(message);
        }
        assertThat(actual, is(notNullValue()));
    }

}
