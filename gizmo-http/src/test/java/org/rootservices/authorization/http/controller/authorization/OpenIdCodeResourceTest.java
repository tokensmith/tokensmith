package org.rootservices.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
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
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.otter.QueryStringToMap;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadOpenIdConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadOpenIdConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();

        servletURI = baseURI + "authorization";
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsCodeShouldReturn200() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());
    }

    @Test
    public void postWhenOpenIdAndResponseTypeCodeAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid";;

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
    public void getWhenOpenIdClientResponseTypeDuplicateStatesShouldReturn302() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8") +
                "&state=some-state" +
                "&state=some-state" +
                "&scope=openid";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(302));

        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=invalid_request" +
                "&error_description=state has more than one value";

        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void getWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        String servletUriWithWrongScopes = this.servletURI +
                "?client_id=" + cc.getClient().getId().toString() +
                "&response_type=" + cc.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid fo0" +
                "&state=some-state";

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
    }

    @Test
    public void getWhenOpenIdClientResponseTypeIsCodeRedirectUriIsWrongShouldReturn404() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=http://rootservices.org/wrong" +
                "&scope=openid";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void postWhenScopesMismatchShouldReturnErrorResponse302() throws Exception {
        // get a session and valid csrf.
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + cc.getClient().getId().toString() +
                "&response_type=" + cc.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid" +
                "&state=some-state";

        Session session = getSessionAndCsrfToken.run(servletURI);

        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletUriWithWrongScopes = this.servletURI +
                "?client_id=" + cc.getClient().getId().toString() +
                "&response_type=" + cc.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(cc.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid foo" +
                "&state=some-state";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletUriWithWrongScopes)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();
        assertThat(response.getStatusCode(), Is.is(302));

        String expectedLocation = "https://rootservices.org" +
                "?error=invalid_scope" +
                "&error_description=scope is not available for this client" +
                "&state=some-state";

        assertThat(response.getHeader("location"), Is.is(notNullValue()));
        assertThat(response.getHeader("location"), Is.is(expectedLocation));
    }

    @Test
    public void postWhenOpenIdExpect200() throws Exception {
        ConfidentialClient confidentialClient = loadOpenIdConfidentialClientWithScopes.run();

        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8") +
                "&scope=openid";

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + "response body: " + e.getResponseBody());
        }
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
        assertThat(location.getScheme(), is(confidentialClient.getClient().getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(confidentialClient.getClient().getRedirectURI().getHost()));
        assertThat(location.getPath(), is(confidentialClient.getClient().getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(params.size(), is(1));
        assertThat(params.get("code").size(), is(1));
        assertThat(params.get("code").get(0), is(notNullValue()));
    }
}
