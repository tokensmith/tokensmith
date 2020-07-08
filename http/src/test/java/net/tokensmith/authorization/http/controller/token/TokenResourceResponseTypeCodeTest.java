package net.tokensmith.authorization.http.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.http.PostTokenCodeGrant;
import helpers.fixture.persistence.http.PostAuthorizationForm;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.fixture.persistence.http.input.AuthEndpointProps;
import helpers.fixture.persistence.http.input.AuthEndpointPropsBuilder;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.authorization.http.response.OpenIdToken;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.http.response.TokenType;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.authorization.http.response.Error;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class TokenResourceResponseTypeCodeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenResourceResponseTypeCodeTest.class);

    private static LoadConfClientCodeResponseType loadConfidentialClientWithScopes;
    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfidentialClientWithScopes;
    private static LoadResourceOwner loadResourceOwner;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static PostAuthorizationForm postAuthorizationForm;
    private static PostTokenCodeGrant postTokenCodeGrant;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    private static TestUtils testUtils;
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static String authServletURI;


    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadConfClientCodeResponseType.class);
        loadOpenIdConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        servletURI = baseURI + "api/public/v1/token";
        authServletURI = baseURI + "authorization";
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        postAuthorizationForm = factoryForPersistence.makePostAuthorizationForm();
        postTokenCodeGrant = factoryForPersistence.makePostTokenCodeGrant();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
        testUtils = new TestUtils();

    }

    public Map<String, List<String>> makeForm(String grantType, String code, String redirectUri) {

        Map<String, List<String>> form = new HashMap<>();
        form.put("grant_type", Arrays.asList(grantType));
        form.put("code", Arrays.asList(code));
        form.put("redirect_uri", Arrays.asList(redirectUri));

        return form;
    }

    @Test
    public void getTokenShouldReturn200() throws Exception {
        ResourceOwner ro = loadResourceOwner.run();
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(confidentialClient)
                .baseURI(authServletURI)
                .scopes(new ArrayList<>())
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm(
                "authorization_code", authorizationCode, confidentialClient.getClient().getRedirectURI().toString()
        );

        String credentials = confidentialClient.getClient().getId().toString() + ":password";

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

        Token token = om.readValue(response.getResponseBody(), Token.class);
        assertThat(token.getTokenType(), is(TokenType.BEARER));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getAccessToken(), is(notNullValue()));
        assertThat(token.getRefreshToken(), is(notNullValue()));
    }

    @Test
    public void getOpenIdTokenShouldReturn200() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");

        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(cc)
                .baseURI(authServletURI)
                .scopes(scopes)
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, cc.getClient().getRedirectURI().toString());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                StandardCharsets.UTF_8
        );


        BoundRequestBuilder requestBuilder = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form);

        ListenableFuture<Response> f = requestBuilder.execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));


        OpenIdToken token = om.readValue(response.getResponseBody(), OpenIdToken.class);
        assertThat(token.getTokenType(), is(TokenType.BEARER));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getAccessToken(), is(notNullValue()));
        assertThat(token.getRefreshToken(), is(notNullValue()));
        assertThat(token.getIdToken(), is(notNullValue()));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<IdToken> jwt = jwtSerde.stringToJwt(token.getIdToken(), IdToken.class);

        String fileName = "build/token-open-id-from-code.txt";
        testUtils.logRequestResponse(fileName, requestBuilder.build(), response, key);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken claims = jwt.getClaims();
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
    public void getOpenIdTokenShouldReturn200AndStateAndNonce() throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");

        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .email(ro.getEmail())
                .confidentialClient(cc)
                .baseURI(authServletURI)
                .scopes(scopes)
                .addQueryParam("state", "state-123")
                .addQueryParam("nonce", "nonce-123")
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, cc.getClient().getRedirectURI().toString());

        String credentials = cc.getClient().getId().toString() + ":password";

        String encodedCredentials = new String(
                Base64.getEncoder().encode(credentials.getBytes()),
                StandardCharsets.UTF_8
        );


        BoundRequestBuilder requestBuilder = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Authorization", "Basic " + encodedCredentials)
                .setFormParams(form);

        ListenableFuture<Response> f = requestBuilder.execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));


        OpenIdToken token = om.readValue(response.getResponseBody(), OpenIdToken.class);
        assertThat(token.getTokenType(), is(TokenType.BEARER));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getAccessToken(), is(notNullValue()));
        assertThat(token.getRefreshToken(), is(notNullValue()));
        assertThat(token.getIdToken(), is(notNullValue()));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<IdToken> jwt = jwtSerde.stringToJwt(token.getIdToken(), IdToken.class);

        String fileName = "build/token-open-id-from-code-with-state-and-nonce.txt";
        testUtils.logRequestResponse(fileName, requestBuilder.build(), response, key);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken claims =  jwt.getClaims();
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
        assertThat(claims.getNonce(), is(notNullValue()));
        assertThat(claims.getNonce().isPresent(), is(true));
        assertThat(claims.getNonce().get(), is("nonce-123"));
    }


    @Test
    public void getTokenWhenCodeIsMissingShouldReturn400() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(confidentialClient)
                .baseURI(authServletURI)
                .scopes(new ArrayList<>())
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, confidentialClient.getClient().getRedirectURI().toString());
        form.remove("code");


        String credentials = confidentialClient.getClient().getId().toString() + ":password";

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

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_request"));
        assertThat(error.getDescription(), is("code is a required field"));
    }

    @Test
    public void getTokenWhenRedirectUriIsMissingShouldReturn400() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(confidentialClient)
                .baseURI(authServletURI)
                .scopes(new ArrayList<>())
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, null);
        form.remove("redirect_uri");

        String credentials = confidentialClient.getClient().getId().toString() + ":password";

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

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_grant"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenMissingAuthenticationHeaderShouldReturn401() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), "application/x-www-form-urlencoded")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));
        assertThat(response.getHeader("WWW-Authenticate"), is("Basic"));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenAuthenticationFailsWrongPasswordShouldReturn401() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(confidentialClient)
                .baseURI(authServletURI)
                .scopes(new ArrayList<>())
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, confidentialClient.getClient().getRedirectURI().toString());

        String credentials = confidentialClient.getClient().getId().toString() + ":wrong-password";

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

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));
        assertThat(response.getHeader("WWW-Authenticate"), is("Basic"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_client"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenAuthCodeNotFoundShouldReturn400() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", "invalid-authorization-code", confidentialClient.getClient().getRedirectURI().toString());

        String credentials = confidentialClient.getClient().getId().toString() + ":password";

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

        assertThat(response.getStatusCode(), is(400));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_grant"));
        assertThat(error.getDescription(), is(nullValue()));
    }

    @Test
    public void getTokenWhenCodeIsCompromisedShouldReturn400() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        // generate a token with a auth code.
        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(confidentialClient)
                .baseURI(authServletURI)
                .scopes(new ArrayList<>())
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);
        Token token = postTokenCodeGrant.run(confidentialClient, servletURI, authorizationCode);

        // attempt to use the auth code a second time.
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        Map<String, List<String>> form = makeForm("authorization_code", authorizationCode, confidentialClient.getClient().getRedirectURI().toString());

        String credentials = confidentialClient.getClient().getId().toString() + ":password";

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

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        Error error = om.readValue(response.getResponseBody(), Error.class);
        assertThat(error.getError(), is("invalid_grant"));
        assertThat(error.getDescription(), is("the authorization code was already used"));
    }
}
