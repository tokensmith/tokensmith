package net.tokensmith.authorization.http.controller;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.repository.entity.ConfidentialClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@Category(ServletContainerTest.class)
public class RegisterResourceTest {

    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfidentialClientWithScopes;
    private static AuthAssertion authAssertion;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        loadOpenIdConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        authAssertion = new AuthAssertion();
        servletURI = baseURI + "register";
    }

    @Test
    public void getNoParametersShouldReturn200() throws Exception {
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void postShouldCreateResourceOwnerAndReturn200() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        String email = UUID.randomUUID().toString() + "@tokensmith.net";
        List<Param> postData = FormFactory.makeRegisterForm(email, "foo", "foo", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));
        assertThat(response.getResponseBody().contains("message"), is(true));
        assertThat(response.getResponseBody().contains("data-status=\"ok\""), is(true));
    }


    @Test
    public void postShouldCreateResourceOwnerAndRedirectToAuthorization() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        // first go to /authorization so we can get the redirect cookie.
        Map<String, String> authParams = Map.ofEntries(
                entry("client_id", confidentialClient.getClient().getId().toString()),
                entry("response_type", confidentialClient.getClient().getResponseTypes().get(0).getName()),
                entry("redirect_uri", URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8")),
                entry("state", "some-state"),
                entry("scope", "openid")
        );
        String authPathWithParams = authAssertion.contextWithParams("authorization", authParams);
        String authServletURI = new StringBuilder()
                .append(baseURI)
                .append(authPathWithParams)
                .toString();

        Session authSession = new Session();
        try {
            authSession = getSessionAndCsrfToken.run(authServletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }
        // end /authorization.

        // now go to /register
        Session registerSession = new Session();
        try {
            registerSession = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        String email = UUID.randomUUID().toString() + "@tokensmith.net";
        List<Param> postData = FormFactory.makeRegisterForm(email, "foo", "foo", registerSession.getCsrfToken());

        // set up cookies so it redirects.
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(authSession.getRedirect());
        cookies.add(registerSession.getSession());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));

        String decodedExpectedRedirect = URLDecoder.decode("/" + authPathWithParams, StandardCharsets.UTF_8);
        String decodedActualRedirect = URLDecoder.decode(response.getHeader("location"), StandardCharsets.UTF_8);
        assertThat("redirect location to /authorization is incorrect", decodedActualRedirect, is(decodedExpectedRedirect));
    }


    @Test
    public void postShouldFailCsrfAndReturn403() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + "response body: " + e.getResponseBody());
        }

        String email = UUID.randomUUID().toString() + "@tokensmith.net";
        List<Param> postData = FormFactory.makeRegisterForm(email, "foo", "foo", "wrong-csrf-token");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_FORBIDDEN));
    }

    @Test
    public void postWhenEmailIsNullShouldShowEmailIsRequiredError() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + "response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeRegisterForm(null, "foo", "foo", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(true));
    }
}
