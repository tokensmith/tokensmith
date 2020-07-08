package net.tokensmith.authorization.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.otter.controller.entity.Cause;
import net.tokensmith.otter.controller.entity.ClientError;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.repository.entity.KeyUse;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.config.AppConfig;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class RSAPublicKeyResourceTest {
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    protected static String servletURI;
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());

    @BeforeClass
    public static void beforeClass() {
        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
        servletURI = baseURI + "api/public/v1/jwk/rsa/";
    }

    @Test
    public void getKeyShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);

        String subjectURI = servletURI + key.getId().toString();
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(subjectURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.OK.getCode()));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        RSAPublicKey actual = om.readValue(response.getResponseBody(), RSAPublicKey.class);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKeyId(), is(notNullValue()));
        assertThat(actual.getUse(), is(KeyUse.SIGNATURE));
        assertThat(actual.getN(), is(notNullValue()));
        assertThat(actual.getE(), is(notNullValue()));
    }

    @Test
    public void getKeysWhenKeyIsNotFoundShouldReturn404() throws Exception {

        String subjectURI = servletURI + UUID.randomUUID().toString();
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(subjectURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));
        assertThat(response.getContentType(), is(ContentType.JSON_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(actual.getCauses().size(), is(1));
        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.URL));
        assertThat(actual.getCauses().get(0).getKey(), is("id"));
        assertThat(actual.getCauses().get(0).getActual(), is(notNullValue()));
    }

    @Test
    public void getKeyWhenIdIsNotAUUIDShouldReturn404() throws Exception {

        String subjectURI = servletURI + "not-a-uuid";
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(subjectURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), "application/json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(actual.getCauses().size(), is(1));
        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.URL));
        assertThat(actual.getCauses().get(0).getActual(), is(notNullValue()));
    }

}