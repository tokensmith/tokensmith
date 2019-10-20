package org.rootservices.authorization.http.controller.resource;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.persistence.entity.ResourceOwner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(ServletContainerTest.class)
public class ForgotPasswordResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    protected static GetSessionAndCsrfToken getSessionAndCsrfToken;

    @BeforeClass
    public static void beforeClass() {
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        servletURI = baseURI + "forgot-password";
    }

    @Test
    public void getShouldBeOk() throws Exception {
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void postShouldBeOk() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<Param> postData = FormFactory.makeForgotPasswordForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));
        assertThat(response.getResponseBody().contains("data-status=\"ok\""), is(true));
    }

    @Test
    public void postWhenEmailIsBlankShouldBeBadRequest() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeForgotPasswordForm("", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getResponseBody().contains("error"), is(true));
        assertThat(response.getResponseBody().contains("data-status=\"ok\""), is(false));
    }


    @Test
    public void postWhenEmailNotFoundShouldBeOk() throws Exception {
        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeForgotPasswordForm("some-email-not-found@rootservices.org", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));
        assertThat(response.getResponseBody().contains("data-status=\"ok\""), is(true));
    }
}