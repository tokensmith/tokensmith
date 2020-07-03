package net.tokensmith.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.publik.LoadOpenIdPublicClient;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.openId.identity.MakeAccessTokenHash;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.QueryStringToMap;


import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by tommackenzie on 6/30/16.
 */
@Category(ServletContainerTest.class)
public class OpenIdImplicitResourceTest {

    private static LoadOpenIdPublicClient loadOpenIdPublicClientWithScopes;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    private static MakeAccessTokenHash makeAccessTokenHash;
    private static AuthAssertion authAssertion;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String contextPath; // path to target endpoint

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadOpenIdPublicClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdPublicClient.class);
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
        makeAccessTokenHash = IntegrationTestSuite.getContext().getBean(MakeAccessTokenHash.class);
        authAssertion = new AuthAssertion();

        contextPath = "authorization";

    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsTokenRedirectUriIsWrongShouldReturn404() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", "http://tokensmith.net/wrong"),
                entry("scope", "openid")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenOpenIdResponseTypeTokenNonceIsMissingShouldReturn302() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("state", "some-state")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));
        String expectedLocation = client.getRedirectURI() +
                "?error=invalid_request" +
                "&error_description=nonce is null" +
                "&state=some-state";

        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsTokenShouldReturn200() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("nonce", "some-nonce")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());
        authAssertion.redirectCookie(response.getCookies(), true, "/" + pathWithParams);
    }

    @Test
    public void postWhenFailsAuthenticationShouldReturn403() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("nonce", "some-nonce")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Param> postData = FormFactory.makeLoginForm("invalid-user@tokensmith.net", session.getCsrfToken());

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getSession());
        cookies.add(session.getRedirect());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(validServletURI)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(403));
        authAssertion.redirectCookie(response.getCookies(), true, "/" + pathWithParams);
    }

    @Test
    public void postWhenClientNotFoundShouldReturn404() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("nonce", "some-nonce")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        Map<String, String> postParams = Map.ofEntries(
                entry("client_id", UUID.randomUUID().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("nonce", "some-nonce")
        );
        String postPathWithParams = authAssertion.contextWithParams(contextPath, postParams);

        String postServletURI = new StringBuilder()
                .append(baseURI)
                .append(postPathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(postServletURI)
                .setFormParams(postData)
                .setCookies(Collections.singletonList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("nonce", "some-nonce"),
                entry("state", "some-state")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        Map<String, String> postParams = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid foo"),
                entry("nonce", "some-nonce"),
                entry("state", "some-state")
        );
        String postPathWithParams = authAssertion.contextWithParams(contextPath, postParams);

        String postServletURI = new StringBuilder()
                .append(baseURI)
                .append(postPathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(postServletURI)
                .setFormParams(postData)
                .setCookies(Collections.singletonList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = "https://tokensmith.net" +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(notNullValue()));
        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenShouldReturnAccessTokenAndIdentity() throws Exception {

        Client client = loadOpenIdPublicClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid email"),
                entry("nonce", "some-nonce")
        );
        String postPathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(postPathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(servletURI);

        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));

        // location scheme, host, and path
        URI location = new URI(response.getHeader("location"));
        assertThat(location.getScheme(), is(client.getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(client.getRedirectURI().getHost()));
        assertThat(location.getPath(), is(client.getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> actualParams = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(actualParams.size(), is(4));
        assertThat(actualParams.get("access_token").size(), is(1));
        assertThat(actualParams.get("access_token").get(0), is(notNullValue()));
        assertThat(actualParams.get("token_type").size(), is(1));
        assertThat(actualParams.get("token_type").get(0), is("bearer"));
        assertThat(actualParams.get("expires_in").get(0), is("3600"));

        assertThat(actualParams.get("id_token").size(), is(1));


        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<IdToken> jwt = jwtSerde.stringToJwt(actualParams.get("id_token").get(0), IdToken.class);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken idToken = jwt.getClaims();
        assertThat(idToken.getNonce().isPresent(), is(true));
        assertThat(idToken.getNonce().get(), is("some-nonce"));
        assertThat(idToken.getAccessTokenHash().isPresent(), is(true));

        String expectedAccessTokenHash = makeAccessTokenHash.makeEncodedHash(actualParams.get("access_token").get(0));
        assertThat(expectedAccessTokenHash, is(idToken.getAccessTokenHash().get()));

        assertThat(idToken.getEmail().isPresent(), is(true));
        assertThat(idToken.getEmail().get(), is(ro.getEmail()));

        // required claims.
        assertThat(idToken.getIssuer().isPresent(), is(true));
        assertThat(idToken.getIssuer().get(), is(EntityFactory.ISSUER));
        assertThat(idToken.getAudience(), is(notNullValue()));
        assertThat(idToken.getAudience().size(), is(1));
        assertThat(idToken.getAudience().get(0), is(client.getId().toString()));
        assertThat(idToken.getExpirationTime().isPresent(), is(true));
        assertThat(idToken.getIssuedAt().isPresent(), is(true));
        assertThat(idToken.getAuthenticationTime(), is(notNullValue()));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenStateIsValidShouldReturnAccessToken() throws Exception {

        Client client = loadOpenIdPublicClientWithScopes.run();
        String state = "test-state";

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token id_token"),
                entry("redirect_uri", URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid email"),
                entry("nonce", "some-nonce"),
                entry("state", state)
        );
        String postPathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(postPathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(servletURI);

        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Collections.singletonList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));

        // location scheme, host, and path
        URI location = new URI(response.getHeader("location"));
        assertThat(location.getScheme(), is(client.getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(client.getRedirectURI().getHost()));
        assertThat(location.getPath(), is(client.getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> actualParams = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(actualParams.size(), is(5));
        assertThat(actualParams.get("access_token").size(), is(1));
        assertThat(actualParams.get("access_token").get(0), is(notNullValue()));
        assertThat(actualParams.get("token_type").size(), is(1));
        assertThat(actualParams.get("token_type").get(0), is("bearer"));
        assertThat(actualParams.get("expires_in").size(), is(1));
        assertThat(actualParams.get("expires_in").get(0), is("3600"));
        assertThat(actualParams.get("state").size(), is(1));
        assertThat(actualParams.get("state").get(0), is(state));

        assertThat(actualParams.get("id_token").size(), is(1));

        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<IdToken> jwt = jwtSerde.stringToJwt(actualParams.get("id_token").get(0), IdToken.class);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken idToken = jwt.getClaims();
        assertThat(idToken.getAccessTokenHash().isPresent(), is(true));

        String expectedAccessTokenHash = makeAccessTokenHash.makeEncodedHash(actualParams.get("access_token").get(0));
        assertThat(expectedAccessTokenHash, is(idToken.getAccessTokenHash().get()));

        assertThat(idToken.getEmail().isPresent(), is(true));
        assertThat(idToken.getEmail().get(), is(ro.getEmail()));

        // required claims.
        assertThat(idToken.getIssuer().isPresent(), is(true));
        assertThat(idToken.getIssuer().get(), is(EntityFactory.ISSUER));
        assertThat(idToken.getAudience(), is(notNullValue()));
        assertThat(idToken.getAudience().size(), is(1));
        assertThat(idToken.getAudience().get(0), is(client.getId().toString()));
        assertThat(idToken.getExpirationTime().isPresent(), is(true));
        assertThat(idToken.getIssuedAt().isPresent(), is(true));
        assertThat(idToken.getAuthenticationTime(), is(notNullValue()));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }



}
