package net.tokensmith.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
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
import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.otter.QueryStringToMap;

import java.net.URI;
import java.util.*;

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

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadPublicClientTokenResponseType = IntegrationTestSuite.getContext().getBean(LoadPublicClientTokenResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();

        servletURI = baseURI + "authorization";
    }

    @Test
    public void getWhenResponseTypeTokenRedirectUriIsWrongShouldReturn404() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token" +
                "&redirect_uri=http://rootservices.org/wrong";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void getWhenClientResponseTypeTokenScopeIsWrongShouldReturn302() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName() +
                "&state=some-state" +
                "&scope=foo";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));
        String expectedLocation = client.getRedirectURI() +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";

        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void getWhenClientResponseTypeIsTokenShouldReturn200() throws Exception {
        Client client = loadPublicClientTokenResponseType.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());
    }

    @Test
    public void postWhenResponseTypeTokenAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName();

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
    public void postWhenResponseTypeTokenClientNotFoundExpect404() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName();

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = this.servletURI +
                "?client_id=" + UUID.randomUUID().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void postWhenResponseTypeTokenAndScopesMismatchExpectErrorResponse() throws Exception {

        // get a session and valid csrf.
        Client client = loadPublicClientTokenResponseType.run();

        String validServletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName() +
                "&state=some-state";

        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName() +
                "&state=some-state" +
                "&scope=foo";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = "https://rootservices.org?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(notNullValue()));
        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void postWhenResponseTypeCodeShouldReturnAccessToken() throws Exception {

        Client client = loadPublicClientTokenResponseType.run();

        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName() +
                "&state=some-state";

        Session session = getSessionAndCsrfToken.run(servletURI);
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

        assertThat(params.size(), is(3));
        assertThat(params.get("access_token").size(), is(1));
        assertThat(params.get("access_token").get(0), is(notNullValue()));
        assertThat(params.get("expires_in").size(), is(1));
        assertThat(params.get("expires_in").get(0), is("3600"));
        assertThat(params.get("state").get(0), is("some-state"));
    }

    @Test
    public void postWhenResponseTypeCodeStateIsValidShouldReturnAccessToken() throws Exception {

        Client client = loadPublicClientTokenResponseType.run();
        String state = "test-state";

        String servletURI = this.servletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=" + client.getResponseTypes().get(0).getName() +
                "&state=" + state;

        Session session = getSessionAndCsrfToken.run(servletURI);
        ResourceOwner ro = loadResourceOwner.run();
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

        assertThat(params.size(), is(3));
        assertThat(params.get("access_token").size(), is(1));
        assertThat(params.get("access_token").get(0), is(notNullValue()));
        assertThat(params.get("expires_in").size(), is(1));
        assertThat(params.get("expires_in").get(0), is("3600"));
        assertThat(params.get("state").size(), is(1));
        assertThat(params.get("state").get(0), is(state));
    }
}
