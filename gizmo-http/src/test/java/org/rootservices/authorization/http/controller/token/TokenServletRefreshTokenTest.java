package org.rootservices.authorization.http.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.http.PostTokenCodeGrant;
import helpers.fixture.persistence.http.PostAuthorizationForm;
import helpers.fixture.persistence.http.PostTokenPasswordGrant;
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
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.config.AppConfig;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.jws.verifier.VerifySignature;
import org.rootservices.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.header.ContentType;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 10/16/16.
 */
@Category(ServletContainerTest.class)
public class TokenServletRefreshTokenTest {
    private static LoadConfClientCodeResponseType loadConfClientWithScopes;
    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfClientWithScopes;
    private static LoadResourceOwner loadResourceOwner;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static PostAuthorizationForm postAuthorizationForm;
    private static PostTokenCodeGrant postTokenCodeGrant;
    private static PostTokenPasswordGrant postTokenPasswordGrant;
    private static HashTextStaticSalt hashText;
    private static TokenRepository tokenRepository;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;

    protected static String servletURI;
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String authServletURI;


    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadConfClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadConfClientCodeResponseType.class);
        loadOpenIdConfClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        postAuthorizationForm = factoryForPersistence.makePostAuthorizationForm();
        postTokenCodeGrant = factoryForPersistence.makePostTokenCodeGrant();
        postTokenPasswordGrant = factoryForPersistence.postPasswordGrant();
        hashText = IntegrationTestSuite.getContext().getBean(HashTextStaticSalt.class);
        tokenRepository = IntegrationTestSuite.getContext().getBean(TokenRepository.class);
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();

        servletURI = baseURI + "api/v1/token";
        authServletURI = baseURI + "authorization";
    }

    public void expireAccessToken(String accessToken) {
        String hashedAccessToken = hashText.run(accessToken);
        tokenRepository.updateExpiresAtByAccessToken(OffsetDateTime.now().minusDays(1), hashedAccessToken);
    }

    public Map<String, List<String>> makeForm(String grantType, String refreshToken) {

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList(grantType));
        form.put("refresh_token", Arrays.asList(refreshToken));

        return form;
    }

    @Test
    public void getTokenShouldReturn200() throws Exception {
        ConfidentialClient cc = loadConfClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        Token token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                StandardCharsets.UTF_8
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));

        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        Token actual = om.readValue(response.getResponseBody(), Token.class);
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getAccessToken(), is(notNullValue()));
    }

    @Test
    public void getOpenIdTokenShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        OpenIdToken token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        OpenIdToken actual = om.readValue(response.getResponseBody(), OpenIdToken.class);
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getAccessToken(), is(notNullValue()));
        assertThat(actual.getRefreshToken(), is(notNullValue()));
        assertThat(actual.getIdToken(), is(notNullValue()));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jwt = jwtSerde.stringToJwt(actual.getIdToken(), IdToken.class);

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
    public void getTokenWhenOriginalWasPasswordGrantShouldReturn200() throws Exception {
        ConfidentialClient cc = loadConfClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        OpenIdToken token = postTokenPasswordGrant.post(
                ro.getEmail(),
                "password",
                "profile",
                cc.getClient().getId().toString(),
                "password",
                servletURI
        );
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Token actual = om.readValue(response.getResponseBody(), Token.class);
        assertThat(actual.getTokenType(), is(TokenType.BEARER));
        assertThat(actual.getExpiresIn(), is(3600L));
        assertThat(actual.getAccessToken(), is(notNullValue()));
    }

    @Test
    public void getTokenWhenRefreshTokenIsMissingShouldReturn400() throws Exception {
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        OpenIdToken token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", null);
        form.remove("refresh_token");

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_request"));
        assertThat(error.getDescription(), is("refresh_token is a required field"));

    }

    @Test
    public void getTokenWhenMissingAuthenticationHeaderShouldReturn401() throws Exception {
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        OpenIdToken token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenClientAuthenticationFailsWrongPasswordShouldReturn401() throws Exception {
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        OpenIdToken token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        String credentials = cc.getClient().getId().toString() + ":wrong-password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenRefreshTokenNotFoundShouldReturn400() throws Exception {
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", "wrong-refresh-token");

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_grant"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenRefreshTokenIsCompromisedShouldReturn400() throws Exception {
        ConfidentialClient cc = loadOpenIdConfClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        scopes.add("openid");

        // generate a token with a auth code.
        String authorizationCode = postAuthorizationForm.run(cc, authServletURI, scopes, ro.getEmail());
        OpenIdToken token = postTokenCodeGrant.run(cc, servletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());

        // prepare the request
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("refresh_token", token.getRefreshToken());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                "UTF-8"
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));
        // end set up test.

        // try to use the same refresh token again.
        f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form)
                .execute();

        response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_grant"));
        assertThat(error.getDescription(), is("the refresh token was already used"));
    }
}
