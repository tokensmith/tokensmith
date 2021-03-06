package net.tokensmith.authorization.http.controller.userInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.category.ServletContainerTest;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.config.AppConfig;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.router.GetServletURI;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class UserInfoResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static GetServletURI getServletURI;
    protected static String servletURI;
    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void beforeClass() {
        getServletURI = new GetServletURI();
        servletURI = baseURI + "api/public/v1/userinfo";

        // prepare the request
        AppConfig config = new AppConfig();
        objectMapper = config.objectMapper();
    }

    @Test
    public void getWhenMissingAuthorizationHeaderShouldReturn401() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), ContentType.JWT.getValue())
                .execute();

        Response response = f.get();

        
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_UNAUTHORIZED));
        assertThat(response.getHeader(Header.AUTH_MISSING.getValue()), is("Bearer"));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));
    }

    @Test
    public void getWhenMissingAcceptsHeaderWithJwtValueShouldReturn404() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_BAD_REQUEST));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));
    }

    @Test
    public void getWhenResourceOwnerNotFoundExceptionShouldReturn401() throws Exception {
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), ContentType.JWT.getValue())
                .setHeader(Header.AUTH.getValue(), "Bearer foo")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_UNAUTHORIZED));
        assertThat(response.getHeader(Header.AUTH_MISSING.getValue()), is(HeaderValue.INVALID_TOKEN.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));
    }
}
