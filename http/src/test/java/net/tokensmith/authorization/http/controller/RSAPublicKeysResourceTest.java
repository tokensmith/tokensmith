package net.tokensmith.authorization.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.otter.controller.entity.Cause;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.http.controller.resource.api.publik.RSAPublicKeysResource;
import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.repository.entity.KeyUse;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.config.AppConfig;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@Category(ServletContainerTest.class)
public class RSAPublicKeysResourceTest {
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    protected static String servletURI;
    protected static String baseURI;

    @BeforeClass
    public static void beforeClass() {
        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
        baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
        servletURI = baseURI + "api/public/v1/jwk/rsa";
    }

    @Test
    public void urlShouldNotMatch() {
        Pattern pattern = Pattern.compile(RSAPublicKeysResource.URL);
        String url = "/api/public/v1/jwk/rsa/foo";
        Matcher m = pattern.matcher(url);

        assertThat(m.matches(), is(false));
    }

    @Test
    public void urlShouldMatch() {
        Pattern pattern = Pattern.compile(RSAPublicKeysResource.URL);
        String url = "/api/public/v1/jwk/rsa";
        Matcher m = pattern.matcher(url);

        assertThat(m.matches(), is(true));
    }

    @Test
    public void getKeysShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), "application/json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        List<RSAPublicKey> actual = om.readValue(response.getResponseBody(),
                om.getTypeFactory().constructCollectionType(List.class, RSAPublicKey.class)
        );
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getKeyId(), is(notNullValue()));
        assertThat(actual.get(0).getUse(), is(KeyUse.SIGNATURE));
        assertThat(actual.get(0).getN(), is(notNullValue()));
        assertThat(actual.get(0).getE(), is(notNullValue()));
    }

    @Test
    public void getKeysWhenPageIsOutOfBoundsShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .addQueryParam("page", "100")
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), "application/json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        List<RSAPublicKey> actual = om.readValue(response.getResponseBody(),
                om.getTypeFactory().constructCollectionType(List.class, RSAPublicKey.class)
        );
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void getKeysWhenPageIsNotANumberShouldReturn400() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .addQueryParam("page", "foo")
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), "application/json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCauses().size(), is(1));
        assertThat(actual.getCauses().get(0).getKey(), is("page"));
        assertThat(actual.getCauses().get(0).getActual(), is("foo"));
        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.URL));
        assertThat(actual.getCauses().get(0).getExpected(), is(notNullValue()));
        assertThat(actual.getCauses().get(0).getExpected().size(), is(0));
        assertThat(actual.getCauses().get(0).getReason(), is("page value is not a integer"));
    }
}