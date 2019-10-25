package org.rootservices.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
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
import org.rootservices.authorization.openId.identity.MakeAccessTokenHash;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.jws.verifier.VerifySignature;
import org.rootservices.jwt.serialization.JwtSerde;
import net.tokensmith.otter.QueryStringToMap;


import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

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

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

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

        servletURI = baseURI + "authorization";
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsTokenRedirectUriIsWrongShouldReturn404() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=http://rootservices.org/wrong" +
                "&scope=openid";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void getWhenOpenIdResponseTypeTokenNonceIsMissingShouldReturn302() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&state=some-state";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));
        String expectedLocation = client.getRedirectURI() +
                "?error=invalid_request" +
                "&error_description=nonce is null" +
                "&state=some-state";

        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsTokenShouldReturn200() throws Exception {
        Client client = loadOpenIdPublicClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&nonce=some-nonce";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());
    }

    @Test
    public void postWhenFailsAuthenticationShouldReturn403() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&nonce=some-nonce";

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Param> postData = FormFactory.makeLoginForm("invalid-user@rootservices.org", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(validServletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(403));
    }

    @Test
    public void postWhenClientNotFoundShouldReturn404() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&nonce=some-nonce";

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = this.servletURI +
                "?client_id=" + UUID.randomUUID().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&nonce=some-nonce";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void postWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {

        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&nonce=some-nonce" +
                "&state=some-state";

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid foo" +
                "&nonce=some-nonce" +
                "&state=some-state";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = "https://rootservices.org" +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(notNullValue()));
        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void postWhenShouldReturnAccessTokenAndIdentity() throws Exception {

        Client client = loadOpenIdPublicClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid email" +
                "&nonce=some-nonce";

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
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(params.size(), is(4));
        assertThat(params.get("access_token").size(), is(1));
        assertThat(params.get("access_token").get(0), is(notNullValue()));
        assertThat(params.get("token_type").size(), is(1));
        assertThat(params.get("token_type").get(0), is("bearer"));
        assertThat(params.get("expires_in").size(), is(1));
        assertThat(params.get("expires_in").get(0), is("3600"));

        assertThat(params.get("id_token").size(), is(1));


        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jwt = jwtSerde.stringToJwt(params.get("id_token").get(0), IdToken.class);

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

        IdToken idToken = (IdToken) jwt.getClaims();
        assertThat(idToken.getNonce().isPresent(), is(true));
        assertThat(idToken.getNonce().get(), is("some-nonce"));
        assertThat(idToken.getAccessTokenHash().isPresent(), is(true));

        String expectedAccessTokenHash = makeAccessTokenHash.makeEncodedHash(params.get("access_token").get(0));
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
    }

    @Test
    public void postWhenStateIsValidShouldReturnAccessToken() throws Exception {

        Client client = loadOpenIdPublicClientWithScopes.run();
        String state = "test-state";

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), "UTF-8") +
                "&scope=openid email" +
                "&nonce=some-nonce" +
                "&state=" + state;

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
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(params.size(), is(5));
        assertThat(params.get("access_token").size(), is(1));
        assertThat(params.get("access_token").get(0), is(notNullValue()));
        assertThat(params.get("token_type").size(), is(1));
        assertThat(params.get("token_type").get(0), is("bearer"));
        assertThat(params.get("expires_in").size(), is(1));
        assertThat(params.get("expires_in").get(0), is("3600"));
        assertThat(params.get("state").size(), is(1));
        assertThat(params.get("state").get(0), is(state));

        assertThat(params.get("id_token").size(), is(1));

        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jwt = jwtSerde.stringToJwt(params.get("id_token").get(0), IdToken.class);

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

        IdToken idToken = (IdToken) jwt.getClaims();
        assertThat(idToken.getAccessTokenHash().isPresent(), is(true));

        String expectedAccessTokenHash = makeAccessTokenHash.makeEncodedHash(params.get("access_token").get(0));
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
    }



}
