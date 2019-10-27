package net.tokensmith.authorization.http.controller.authorization;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.*;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.otter.QueryStringToMap;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        loadConfidentialClientWithScopes = IntegrationTestSuite.getContext().getBean(LoadConfClientCodeResponseType.class);
        loadResourceOwner = IntegrationTestSuite.getContext().getBean(LoadResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();

        servletURI = baseURI + "authorization";
    }

    @Test
    public void getWhenResponseTypeCodeRedirectUriIsOkExpect200() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&redirect_uri=" + URLEncoder.encode(confidentialClient.getClient().getRedirectURI().toString(), "UTF-8");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    public void getWhenResponseTypeCodeRedirectUriIsWrongShouldReturn404() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=code" +
                "&redirect_uri=http://rootservices.org/wrong";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public void getWhenClientResponseTypeIsCodeRequestResponseTypeTokenShouldReturn302() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=token" +
                "&state=some-state";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));
        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=unauthorized_client" +
                "&error_description=response_type provided does not match client response type" +
                "&state=some-state";
        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void getWhenClientResponseTypeCodeShouldReturn200() throws Exception {
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient().prepareGet(servletURI).execute();
        Response response = f.get();
        assertThat(response.getStatusCode(), is(200));

        Optional<String> csrfToken = getSessionAndCsrfToken.extractCsrfToken(response.getResponseBody());
        assertTrue(csrfToken.isPresent());
    }

    @Test
    public void postWhenResponseTypeCodeAndFailsAuthenticationThen403() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName();

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
    public void postResponseTypeCodeWhenRequestResponseTypeTokenExpect302() throws Exception {

        // get a session and valid csrf.
        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        String validServletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
                "&state=some-state";
        Session session = getSessionAndCsrfToken.run(validServletURI);

        ResourceOwner ro = loadResourceOwner.run();
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&state=some-state" +
                "&response_type=token";

        // make request with wrong response type.
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        String expectedLocation = confidentialClient.getClient().getRedirectURI() +
                "?error=unauthorized_client" +
                "&error_description=response_type provided does not match client response type" +
                "&state=some-state";

        Response response = f.get();
        assertThat(response.getStatusCode(), is(302));
        assertThat(response.getHeader("location"), is(expectedLocation));
    }

    @Test
    public void postWhenResponseTypeCodeShouldReturnAuthCode() throws Exception {

        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();

        ResourceOwner ro = loadResourceOwner.run();

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName();

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

    @Test
    public void postWhenResponseTypeCodeStateIsValidExpectAuthCode() throws Exception {

        ConfidentialClient confidentialClient = loadConfidentialClientWithScopes.run();
        String state = "test-state";

        String servletURI = this.servletURI +
                "?client_id=" + confidentialClient.getClient().getId().toString() +
                "&response_type=" + confidentialClient.getClient().getResponseTypes().get(0).getName() +
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
        assertThat(location.getScheme(), is(confidentialClient.getClient().getRedirectURI().getScheme()));
        assertThat(location.getHost(), is(confidentialClient.getClient().getRedirectURI().getHost()));
        assertThat(location.getPath(), is(confidentialClient.getClient().getRedirectURI().getPath()));

        //authorization code.
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        assertThat(params.size(), is(2));
        assertThat(params.get("code").size(), is(1));
        assertThat(params.get("code").get(0), is(notNullValue()));
        assertThat(params.get("state").size(), is(1));
        assertThat(params.get("state").get(0), is(state));
    }
}
