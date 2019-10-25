package org.rootservices.authorization.http.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadConfClientPasswordResponseType;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientPasswordResponseType;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.http.response.Error;
import org.rootservices.authorization.http.response.OpenIdToken;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.http.response.TokenType;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.config.AppConfig;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.jws.verifier.VerifySignature;
import org.rootservices.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class TokenServletResponseTypePasswordTest {

    private static LoadConfClientPasswordResponseType loadConfClientPasswordResponseType;
    private static LoadResourceOwner loadResourceOwner;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static LoadOpenIdConfClientPasswordResponseType loadOpenIdConfClientPasswordResponseType;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        ApplicationContext ap = IntegrationTestSuite.getContext();
        loadConfClientPasswordResponseType = ap.getBean(LoadConfClientPasswordResponseType.class);
        loadResourceOwner = ap.getBean(LoadResourceOwner.class);
        loadOpenIdResourceOwner = ap.getBean(LoadOpenIdResourceOwner.class);
        loadOpenIdConfClientPasswordResponseType = ap.getBean(LoadOpenIdConfClientPasswordResponseType.class);
        servletURI = baseURI + "api/v1/token";
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
    }

    @Test
    public void getTokenShouldReturn200() throws Exception {
        ResourceOwner ro = loadResourceOwner.run();
        ConfidentialClient cc = loadConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("username", Arrays.asList(ro.getEmail()));
        form.put("password", Arrays.asList("password"));
        form.put("scope", Arrays.asList(scope));

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        Token token = om.readValue(response.getResponseBody(), Token.class);
        assertThat(token.getTokenType(), is(TokenType.BEARER));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getAccessToken(), is(notNullValue()));
    }

    @Test
    public void getOpenIdTokenShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        ConfidentialClient cc = loadOpenIdConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("username", Arrays.asList(ro.getEmail()));
        form.put("password", Arrays.asList("password"));
        form.put("scope", Arrays.asList(scope));

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        OpenIdToken token = om.readValue(response.getResponseBody(), OpenIdToken.class);
        assertThat(token.getTokenType(), is(TokenType.BEARER));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getAccessToken(), is(notNullValue()));
        assertThat(token.getIdToken(), is(notNullValue()));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jwt = jwtSerde.stringToJwt(token.getIdToken(), IdToken.class);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                KeyType.RSA,
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken claims = (IdToken) jwt.getClaims();
        assertThat(claims.getEmail().isPresent(), is(true));
        assertThat(claims.getEmail().get(), is(ro.getEmail()));
        assertThat(claims.getEmailVerified().isPresent(), is(true));
        assertThat(claims.getEmailVerified().get(), is(false));

        // required claims.
        assertThat(claims.getIssuer().isPresent(), is(true));
        assertThat(claims.getIssuer().get(), is(EntityFactory.ISSUER));
        assertThat(claims.getAudience(), is(notNullValue()));
        assertThat(claims.getAudience().size(), is(1));
        assertThat(claims.getAudience().get(0), is(cc.getClient().getId().toString()));
        assertThat(claims.getExpirationTime().isPresent(), is(true));
        assertThat(claims.getIssuedAt().isPresent(), is(true));
        assertThat(claims.getAuthenticationTime(), is(notNullValue()));
    }

    @Test
    public void getTokenWhenUserNameMissingShouldReturn400() throws Exception {
        ResourceOwner ro = loadResourceOwner.run();
        ConfidentialClient cc = loadConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("password", Arrays.asList("password"));
        form.put("scope", Arrays.asList(scope));

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_request"));
        assertThat(error.getDescription(), is("username is a required field"));
    }

    @Test
    public void getTokenWhenPasswordMissingShouldReturn400() throws Exception {
        ResourceOwner ro = loadResourceOwner.run();
        ConfidentialClient cc = loadConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("username", Arrays.asList(ro.getEmail()));
        form.put("scope", Arrays.asList(scope));

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_request"));
        assertThat(error.getDescription(), is("password is a required field"));
    }

    @Test
    public void getTokenWhenMissingAuthenticationHeaderShouldReturn401() throws Exception {
        ResourceOwner ro = loadResourceOwner.run();
        ConfidentialClient cc = loadConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("password", Arrays.asList("password"));
        form.put("scope", Arrays.asList(scope));

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenClientAuthenticationFailsWrongPasswordShouldReturn401() throws Exception {
        ConfidentialClient cc = loadConfClientPasswordResponseType.run();

        String scope = cc.getClient().getScopes().stream().map(item -> item.getName()).collect(Collectors.joining(" "));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList("password"));
        form.put("password", Arrays.asList("password"));
        form.put("scope", Arrays.asList(scope));

        String credentials = cc.getClient().getId().toString() + ":wrong-password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.FORM_URL_ENCODED.getValue())
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }
}
