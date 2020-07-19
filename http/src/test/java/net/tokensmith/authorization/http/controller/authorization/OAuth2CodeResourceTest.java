package net.tokensmith.authorization.http.controller.authorization;

import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.ResourceOwner;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by tommackenzie on 6/30/16.
 */
@Category(ServletContainerTest.class)
public class OAuth2CodeResourceTest {

    private static LoadConfClientCodeResponseType loadConfidentialClientWithScopes;
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

        loadConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        authAssertion = new AuthAssertion();

        contextPath = "authorization";
    }

    @Test
    public void getWhenResponseTypeCodeRedirectUriIsOkExpect200() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
            entry("client_id", confidentialClient.getClient().getId().toString()),
            entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
            entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8"))
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();
        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        authAssertion.redirectCookie(response.getCookies(), true, "/" + pathWithParams);
    }

    @Test
    public void getWhenResponseTypeCodeRedirectUriIsWrongShouldReturn404() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", "code"),
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
    public void getWhenClientResponseTypeIsCodeRequestResponseTypeTokenShouldReturn302() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", "token"),
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
        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=unauthorized_client" +
                "&error_description=response_type provided does not match client response type" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(expectedLocation));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void getWhenClientResponseTypeCodeShouldReturn200() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName())
        );
        String pathWithParams = authAssertion.contextWithParams(contextPath, params);

        String servletURI = new StringBuilder()
                .append(baseURI)
                .append(pathWithParams)
                .toString();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());

        authAssertion.redirectCookie(response.getCookies(), true, "/" + pathWithParams);
    }

    @Test
    public void postWhenResponseTypeCodeAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName())
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
    public void postResponseTypeCodeWhenRequestResponseTypeTokenExpectErrorAnd302() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
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
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", "token"),
                entry("state", "some-state")
        );
        String postPathWithParams = authAssertion.contextWithParams(contextPath, postParams);
        String postServletURI = new StringBuilder()
                .append(baseURI)
                .append(postPathWithParams)
                .toString();

        // make request with wrong response type.
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(postServletURI)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=unauthorized_client" +
                "&error_description=response_type provided does not match client response type" +
                "&state=some-state";

        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));
        assertThat(response.getHeader("location"), is(expectedLocation));

        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenResponseTypeCodeShouldReturnAuthCode() throws Exception {

        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        ResourceOwner ro = loadResourceOwner.run();

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName())
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

    @Test
    public void postWhenResponseTypeCodeStateIsValidExpectAuthCode() throws Exception {

        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        String state = "test-state";

        Map<String, String> params = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("state", state)
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
        assertThat(location.getScheme(), is(confidentialClient.getClient().getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(confidentialClient.getClient().getRedirectURI().getHost()));
        assertThat(location.getPath(), is(confidentialClient.getClient().getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> actualParams = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(actualParams.size(), is(2));
        assertThat(actualParams.get("code").size(), is(1));
        assertThat(actualParams.get("code").get(0), is(notNullValue()));
        assertThat(actualParams.get("state").size(), is(1));
        assertThat(actualParams.get("state").get(0), is(state));

        authAssertion.redirectCookie(response.getCookies(), false, null);
    }
}
