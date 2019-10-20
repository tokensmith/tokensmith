package org.rootservices.authorization.http.controller.userInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.register.request.UserInfo;
import org.rootservices.config.AppConfig;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.GetServletURI;


import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
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
        servletURI = baseURI + "api/v1/userinfo";

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

    @Test
    public void postWhenNoPayloadShouldReturn400() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_BAD_REQUEST));

        // TODO: otter fix this test
        /**
        Error error = objectMapper.readValue(response.getResponseBody(), Error.class);
        assertThat(error, is(notNullValue()));
        assertThat(error.getError(), is("Invalid Payload"));
        assertThat(error.getDescription(), is(nullValue()));
         **/
    }

    @Test
    public void postWhenEmailTakenExistsShouldReturn400() throws Exception {
        UserInfo payload = new UserInfo();

        String email = UUID.randomUUID().toString() + "@rootservices.org";
        String password = "password";

        payload.setEmail(email);
        payload.setPassword(password);

        Response response = null;
        for(int i=0; i<=1; i++) {
            ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                    .preparePost(servletURI)
                    .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                    .setBody(objectMapper.writeValueAsBytes(payload))
                    .execute();

            response = f.get();
        }

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_BAD_REQUEST));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        // TODO: otter fix this test
        /**
        Error error = objectMapper.readValue(response.getResponseBody(), Error.class);
        assertThat(error, is(notNullValue()));
        assertThat(error.getError(), is("Registration Error"));
        assertThat(error.getDescription(), is("Could not insert resource_owner"));
         */
    }

    @Test
    public void postWhenMinPayloadShouldReturn200() throws Exception {
        UserInfo payload = new UserInfo();

        String email = UUID.randomUUID().toString() + "@rootservices.org";
        String password = "password";

        payload.setEmail(email);
        payload.setPassword(password);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody(objectMapper.writeValueAsBytes(payload))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_CREATED));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));
    }

    @Test
    public void postWhenMaxPayloadShouldReturn200() throws Exception {
        UserInfo payload = EntityFactory.makeFullUserInfo();

        String email = UUID.randomUUID().toString() + "@rootservices.org";
        String password = "password";

        payload.setEmail(email);
        payload.setPassword(password);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody(objectMapper.writeValueAsBytes(payload))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_CREATED));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));
    }
}
