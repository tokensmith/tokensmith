package net.tokensmith.authorization.http.controller.authorization;


import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import helpers.assertion.AuthAssertion;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;


import org.asynchttpclient.Response;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.repository.entity.*;



import java.util.*;
import java.util.concurrent.ExecutionException;


import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by tommackenzie on 4/1/15.
 */
@Category(ServletContainerTest.class)
public class AuthorizationResourceTest {

    private static LoadConfClientCodeResponseType loadConfidentialClientWithScopes;
    private static LoadResourceOwner loadResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static AuthAssertion authAssertion;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
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
        servletURI = baseURI + contextPath;
    }

    @Test
    public void getNoParametersShouldReturn404() throws Exception {
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }


    @Test
    public void postWhenNoSessionAndWrongCsrfTokenExpectCsrfFailureAnd403() throws Exception, ExecutionException, InterruptedException {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName();

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), "redirect");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_FORBIDDEN));
        authAssertion.redirectCookie(response.getCookies(), false, null);
    }

    @Test
    public void postWhenWrongCsrfTokenExpectCsrfFailureAnd403() throws Exception {
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

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + " response body: " + e.getResponseBody());
        }

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getRedirect());
        cookies.add(session.getCsrf());

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), "redirect");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(cookies)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_FORBIDDEN));

        authAssertion.redirectCookie(response.getCookies(), true, "/" + pathWithParams);
    }
}