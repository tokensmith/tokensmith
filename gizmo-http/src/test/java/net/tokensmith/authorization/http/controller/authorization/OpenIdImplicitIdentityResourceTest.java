package net.tokensmith.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.publik.LoadOpenIdPublicClientIdToken;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.repository.entity.*;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.QueryStringToMap;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 6/30/16.
 */
@Category(ServletContainerTest.class)
public class OpenIdImplicitIdentityResourceTest {

    private static LoadOpenIdPublicClientIdToken loadOpenIdPublicClientIdTokenWithScopes;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;


    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadOpenIdPublicClientIdTokenWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdPublicClientIdToken.class);
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();

        servletURI = baseURI + "authorization";
    }

    private String servletURI(UUID clientId, URI redirectUri, String scope) throws UnsupportedEncodingException {
        return this.servletURI +
                "?client_id=" + clientId.toString() +
                "&response_type=id_token" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri.toString(), "UTF-8") +
                "&scope=" + scope;
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsIdTokenRedirectUriIsWrongShouldReturn404() throws Exception {
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();

        String servletURI = servletURI(client.getId(), new URI("http://rootservices.org/wrong"), "openid")
            + "&nonce=some-nonce";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void getWhenOpenIdResponseTypeIdTokenNonceIsMissingShouldReturn302() throws Exception {
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();

        String servletURI = servletURI(client.getId(), client.getRedirectURI(), "openid") +
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
    public void getWhenOpenIdClientResponseTypeIsIdTokenShouldReturn200() throws Exception {
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();

        String servletURI = servletURI(client.getId(), client.getRedirectURI(), "openid")
                + "&nonce=some-nonce";;

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertThat(csrfToken.isPresent(), is(true));
    }

    @Test
    public void postWhenFailsAuthenticationShouldReturn403() throws Exception {
        // get a session and valid csrf.
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();
        String validServletURI = servletURI(client.getId(), client.getRedirectURI(), "openid")
                + "&nonce=some-nonce";;
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
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();
        String validServletURI = servletURI(client.getId(), client.getRedirectURI(), "openid")
                + "&nonce=some-nonce";;
        Session session = getSessionAndCsrfToken.run(validServletURI);
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = servletURI(UUID.randomUUID(), client.getRedirectURI(), "openid");

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
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();

        String validServletURI = servletURI(client.getId(), client.getRedirectURI(), "openid") +
            "&nonce=some-nonce" +
            "&state=some-state";

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadOpenIdResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = servletURI(client.getId(), client.getRedirectURI(), "openid foo") +
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
    public void postWhenShouldReturnIdentity() throws Exception {
        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();

        String servletURI = servletURI(client.getId(), client.getRedirectURI(), "openid email")
                + "&nonce=some-nonce";;

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

        assertThat(params.size(), is(1));
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
        assertThat(idToken.getAccessTokenHash().isPresent(), is(false));

        assertThat(idToken.getEmail().isPresent(), is(true));
        assertThat(idToken.getEmail().get(), is(ro.getEmail()));

        // required claims.
        assertThat(idToken.getIssuer().isPresent(), is(true));
        assertThat(idToken.getIssuer().get(), is(EntityFactory.ISSUER));
        assertThat(idToken.getAudience(), is(notNullValue()));
        assertThat(idToken.getAudience().size(), is(1));
        assertThat(idToken.getAudience().get(0), is(client.getId().toString()));
        assertThat(idToken.getIssuedAt().isPresent(), is(true));
        assertThat(idToken.getExpirationTime().isPresent(), is(true));
        assertThat(idToken.getAuthenticationTime(), is(notNullValue()));
    }


    @Test
    public void postWhenStateIsValidShouldReturnAccessToken() throws Exception {

        Client client = loadOpenIdPublicClientIdTokenWithScopes.run();
        String state = "test-state";
        String servletURI = servletURI(client.getId(), client.getRedirectURI(), "openid email")
                + "&state=" + state + "&nonce=some-nonce";
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

        assertThat(params.size(), is(2));
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
        assertThat(idToken.getAccessTokenHash().isPresent(), is(false));
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
