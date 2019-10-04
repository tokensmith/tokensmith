package org.rootservices.authorization.http.controller;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.router.GetServletURI;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@Category(ServletContainerTest.class)
public class RegisterResourceTest {

    private static GetSessionAndCsrfToken getSessionAndCsrfToken;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
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

        String email = UUID.randomUUID().toString() + "@rootservices.org";
        List<Param> postData = FormFactory.makeRegisterForm(email, "foo", "foo", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));
    }

    @Test
    public void postShouldFailCsrfAndReturn403() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + "response body: " + e.getResponseBody());
        }

        String email = UUID.randomUUID().toString() + "@rootservices.org";
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
