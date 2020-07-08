package net.tokensmith.authorization.http.controller.authorization;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import io.netty.handler.codec.http.cookie.Cookie;
import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.hamcrest.core.Is;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.otter.QueryStringToMap;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by tommackenzie on 6/30/16.
 */
@Category(ServletContainerTest.class)
public class OpenIdCodeResourceTest {

    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfidentialClientWithScopes;
    private static LoadResourceOwner loadResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static AuthAssertion authAssertion;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String contextPath; // path to target endpoint

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadOpenIdConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        authAssertion = new AuthAssertion();

        contextPath = "authorization";
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsCodeShouldReturn200() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid")
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
    public void postWhenOpenIdAndResponseTypeCodeAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Param> postData = FormFactory.makeLoginForm("invalid-user@tokensmith.net", session.getCsrfToken());

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

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
    public void getWhenOpenIdClientResponseTypeDuplicateStatesShouldReturn302() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("state", "some-state"),
                entry("scope", "openid")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params)
                + "&state=some-state"; // <-- extra state param

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=invalid_request" +
                "&error_description=state has more than one value";

        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", cc.getClient().getId().toString()),
                entry("response_type", cc.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid fo0"),
                entry("state", "some-state")

        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletUriWithWrongScopes = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletUriWithWrongScopes)
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), Is.is(302));

        String expectedLocation = cc.getClient().getRedirectURI() +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";

        assertThat(response.getHeader("location"), Is.is(notNullValue()));
        assertThat(response.getHeader("location"), Is.is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsCodeRedirectUriIsWrongShouldReturn404() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
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
    public void postWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {
        // get a session and valid csrf.
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", cc.getClient().getId().toString()),
                entry("response_type", cc.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid"),
                entry("state", "some-state")

        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(servletURI);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        Map<String, String> postParams = Map.ofEntries(
                entry("client_id", cc.getClient().getId().toString()),
                entry("response_type", cc.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid foo"),
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
                .setCookies(cookies)
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), Is.is(302));

        String expectedLocation = "https://tokensmith.net" +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";

        assertThat(response.getHeader("location"), Is.is(notNullValue()));
        assertThat(response.getHeader("location"), Is.is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenOpenIdExpectCodeAnd302() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        ResourceOwner ro = loadResourceOwner.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("scope", "openid")

        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();


        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + "response body: " + e.getResponseBody());
        }
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));

        // location scheme, host, and path
        URI location = new URI(response.getHeader("location"));
        assertThat(location.getScheme(), is(confidentialClient.getClient().getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(confidentialClient.getClient().getRedirectURI().getHost()));
        assertThat(location.getPath(), is(confidentialClient.getClient().getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> actualParams = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(actualParams.size(), is(1));
        assertThat(actualParams.get("code").size(), is(1));
        assertThat(actualParams.get("code").get(0), is(notNullValue()));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }
}
