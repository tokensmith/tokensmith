package net.tokensmith.authorization.http.controller.authorization;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import io.netty.handler.codec.http.cookie.Cookie;
import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.publik.LoadPublicClientTokenResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.otter.QueryStringToMap;

import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by tommackenzie on 6/30/16.
 */
@Category(ServletContainerTest.class)
public class OAuth2ImplicitResourceTest {

    private static LoadPublicClientTokenResponseType loadPublicClientTokenResponseType;
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

        loadPublicClientTokenResponseType = IntegrationTestSuite.getContext().getBean(LoadPublicClientTokenResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        authAssertion = new AuthAssertion();

        contextPath = "authorization";
    }

    @Test
    public void getWhenResponseTypeTokenRedirectUriIsWrongShouldReturn404() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", "token"),
                entry("redirect_uri", "http://tokensmith.net/wrong")
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
    public void getWhenClientResponseTypeTokenScopeIsWrongShouldReturn302() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName()),
                entry("state", "some-state"),
                entry("scope", "foo")
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
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";

        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenClientResponseTypeIsTokenShouldReturn200() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName())
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
    public void postWhenResponseTypeTokenAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName())
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Param> postData = FormFactory.makeLoginForm("invalid-user@tokensmith.net", session.getCsrfToken());

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getCsrf());
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
    public void postWhenResponseTypeTokenClientNotFoundExpect404() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName())
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        Map<String, String> postParams = Map.ofEntries(
                entry("client_id", UUID.randomUUID().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName())
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
        assertThat(response.getStatusCode(), is(404));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenResponseTypeTokenAndScopesMismatchExpectErrorResponse() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName()),
                entry("state", "some-state")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String validServletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        Map<String, String> postParams = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName()),
                entry("state", "some-state"),
                entry("scope", "foo")
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
        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = "https://tokensmith.net?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(notNullValue()));
        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenResponseTypeCodeShouldReturnAccessToken() throws Exception {

        Client client = loadPublicClientTokenResponseType.run();

        ResourceOwner ro = loadResourceOwner.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName()),
                entry("state", "some-state")
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(servletURI);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getCsrf());
        cookies.add(session.getRedirect());

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(cookies)
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

        assertThat(params.size(), is(3));
        assertThat(actualParams.get("access_token").size(), is(1));
        assertThat(actualParams.get("access_token").get(0), is(notNullValue()));
        assertThat(actualParams.get("expires_in").size(), is(1));
        assertThat(actualParams.get("expires_in").get(0), is("3600"));
        assertThat(actualParams.get("state").get(0), is("some-state"));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenResponseTypeCodeStateIsValidShouldReturnAccessToken() throws Exception {

        Client client = loadPublicClientTokenResponseType.run();
        String state = "test-state";

        Map<String, String> params = Map.ofEntries(
                entry("client_id", client.getId().toString()),
                entry("response_type", client.getResponseTypes().get(0).getName()),
                entry("state", state)
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        Session session = getSessionAndCsrfToken.run(servletURI);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getCsrf());
        cookies.add(session.getRedirect());

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(cookies)
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

        assertThat(actualParams.size(), is(3));
        assertThat(actualParams.get("access_token").size(), is(1));
        assertThat(actualParams.get("access_token").get(0), is(notNullValue()));
        assertThat(actualParams.get("expires_in").size(), is(1));
        assertThat(actualParams.get("expires_in").get(0), is("3600"));
        assertThat(actualParams.get("state").size(), is(1));
        assertThat(actualParams.get("state").get(0), is(state));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }
}
