package net.tokensmith.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.client.publik.LoadPublicClientTokenResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;


import com.ning.http.client.Response;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.repository.entity.*;



import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by tommackenzie on 4/1/15.
 */
@Category(ServletContainerTest.class)
public class AuthorizationResourceTest {

    private static LoadConfClientCodeResponseType loadConfidentialClientWithScopes;
    private static LoadPublicClientTokenResponseType loadPublicClientTokenResponseType;
    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfidentialClientWithScopes;
    private static LoadResourceOwner loadResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
            IntegrationTestSuite.getContext()
        );

        loadConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadConfClientCodeResponseType.class);
        loadPublicClientTokenResponseType = IntegrationTestSuite.getContext().getBean(LoadPublicClientTokenResponseType.class);
        loadOpenIdConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();

        servletURI = baseURI + "authorization";
    }

    @Test
    public void getNoParametersShouldReturn404() throws Exception {
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
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
    }

    @Test
    public void postWhenWrongCsrfTokenExpectCsrfFailureAnd403() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName();

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + " response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), "redirect");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_FORBIDDEN));
    }
}