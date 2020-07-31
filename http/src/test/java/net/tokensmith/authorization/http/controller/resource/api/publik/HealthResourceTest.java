package net.tokensmith.authorization.http.controller.resource.api.publik;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.category.ServletContainerTest;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.authorization.http.controller.resource.api.publik.model.Health;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Address;
import net.tokensmith.config.AppConfig;
import org.apache.commons.httpclient.HttpStatus;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@Category(ServletContainerTest.class)
public class HealthResourceTest {
    protected static String baseURI;
    protected static String servletURI;

    @BeforeClass
    public static void beforeClass() {
        baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
        servletURI = baseURI + "api/public/v1/health";
    }

    @Test
    public void getShouldBeOk() throws Exception {
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader("Content-Type", "application/json; charset=utf-8;")
                .setHeader("Accept", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));

        Health actual = om.readerFor(Health.class).readValue(response.getResponseBody());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatus(), is(Health.Status.UP));
    }
}