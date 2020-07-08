package helpers.suite;


import config.TestHttpAppConfig;
import helpers.category.UnitTests;
import helpers.category.ServletContainerTest;
import net.tokensmith.authorization.http.controller.resource.api.site.RestAddressResourceTest;
import net.tokensmith.authorization.http.controller.resource.api.site.RestProfileResourceTest;
import net.tokensmith.otter.server.HttpServerConfig;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import net.tokensmith.authorization.http.controller.RSAPublicKeyResourceTest;
import net.tokensmith.authorization.http.controller.RSAPublicKeysResourceTest;
import net.tokensmith.authorization.http.controller.RegisterResourceTest;
import net.tokensmith.authorization.http.controller.WelcomeResourceTest;
import net.tokensmith.authorization.http.controller.authorization.*;
import net.tokensmith.authorization.http.controller.resource.ForgotPasswordResourceTest;
import net.tokensmith.authorization.http.controller.resource.UpdatePasswordResourceTest;
import net.tokensmith.authorization.http.controller.resource.html.authorization.AuthorizationResource;
import net.tokensmith.authorization.http.controller.token.TokenResourceRefreshTokenTest;
import net.tokensmith.authorization.http.controller.token.TokenResourceResponseTypeCodeTest;
import net.tokensmith.authorization.http.controller.token.TokenResourceResponseTypePasswordTest;
import net.tokensmith.authorization.http.controller.userInfo.*;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.server.container.ServletContainer;
import net.tokensmith.otter.server.container.ServletContainerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.util.Arrays;
import java.util.List;

import static org.asynchttpclient.Dsl.asyncHttpClient;


@RunWith(Categories.class)
@Categories.IncludeCategory(ServletContainerTest.class)
@Categories.ExcludeCategory(UnitTests.class)
@Suite.SuiteClasses({
        AuthorizationResourceTest.class,
        OAuth2CodeResourceTest.class,
        OAuth2ImplicitResourceTest.class,
        OpenIdCodeResourceTest.class,
        OpenIdImplicitIdentityResourceTest.class,
        OpenIdImplicitResourceTest.class,
        TokenResourceResponseTypeCodeTest.class,
        TokenResourceResponseTypePasswordTest.class,
        TokenResourceRefreshTokenTest.class,
        UserInfoResourceTest.class,
        UserInfoResourceOpenIdPasswordTest.class,
        UserInfoResourceOpenIdCodeTest.class,
        UserInfoResourceOpenIdTokenAndIdTokenTest.class,
        UserInfoResourceOpenIdRefreshTest.class,
        UserInfoResourceOAuth2CodeTest.class,
        UserInfoResourceOAuth2PasswordTest.class,
        UserInfoResourceOAuth2RefreshTest.class,
        UserInfoResourceOAuth2TokenTest.class,
        RSAPublicKeysResourceTest.class,
        RSAPublicKeyResourceTest.class,
        RegisterResourceTest.class,
        WelcomeResourceTest.class,
        ForgotPasswordResourceTest.class,
        UpdatePasswordResourceTest.class,
        RestProfileResourceTest.class,
        RestAddressResourceTest.class
})
public class IntegrationTestSuite {

    private static OtterAppFactory otterTestAppFactory;
    private static ServletContainerFactory servletContainerFactory;
    private static ServletContainer server;
    private static AsyncHttpClient httpClient;
    private static String DOCUMENT_ROOT = "/";
    private static int RANDOM_PORT = 0;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    private static AnnotationConfigApplicationContext context;

    private static void configureAndStartServletContainer() throws Exception {

        otterTestAppFactory = new OtterAppFactory();
        servletContainerFactory = otterTestAppFactory.servletContainerFactory();

        List<String> gzipMimeTypes = Arrays.asList(
                "text/html", "text/plain", "text/xml",
                "text/css", "application/javascript", "text/javascript",
                "application/json");


        HttpServerConfig config = new HttpServerConfig.Builder()
                .documentRoot(DOCUMENT_ROOT)
                .port(RANDOM_PORT)
                .requestLog(REQUEST_LOG)
                .clazz(AuthorizationResource.class)
                .gzipMimeTypes(gzipMimeTypes)
                .build();

        server = servletContainerFactory.makeServletContainer(config);

        server.start();

        httpClient = asyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setCookieStore(null).build());
    }

    /**
     * Starts a servlet container and a spring container.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        configureAndStartServletContainer();
        context = new AnnotationConfigApplicationContext();
        context.register(TestHttpAppConfig.class);
        context.refresh();
    }

    /**
     * Stops a servlet container
     *
     * @throws Exception
     */
    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
    }

    public static ServletContainer getServer() {
        return server;
    }

    public static void setServer(ServletContainer server) {
        IntegrationTestSuite.server = server;
    }

    public static AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    public static void setHttpClient(AsyncHttpClient httpClient) {
        IntegrationTestSuite.httpClient = httpClient;
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }
}
