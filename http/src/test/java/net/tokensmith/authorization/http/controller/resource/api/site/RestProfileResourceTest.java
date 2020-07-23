package net.tokensmith.authorization.http.controller.resource.api.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.category.ServletContainerTest;
import helpers.fixture.ModelFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientCodeResponseType;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.http.PostAuthorizationForm;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.http.input.AuthEndpointProps;
import helpers.fixture.persistence.http.input.AuthEndpointPropsBuilder;
import helpers.suite.IntegrationTestSuite;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Name;
import net.tokensmith.authorization.http.controller.resource.api.site.model.Profile;
import net.tokensmith.config.AppConfig;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.ResourceOwner;
import org.apache.commons.httpclient.HttpStatus;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@Category(ServletContainerTest.class)
public class RestProfileResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static String authServletURI;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;
    protected static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static LoadOpenIdConfClientCodeResponseType loadOpenIdConfidentialClientWithScopes;
    protected static PostAuthorizationForm postAuthorizationForm;


    @BeforeClass
    public static void beforeClass() {
        ApplicationContext ac = IntegrationTestSuite.getContext();
        loadOpenIdConfidentialClientWithScopes = ac.getBean(LoadOpenIdConfClientCodeResponseType.class);
        loadOpenIdResourceOwner = ac.getBean(LoadOpenIdResourceOwner.class);

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
        postAuthorizationForm = factoryForPersistence.makePostAuthorizationForm();
        servletURI = baseURI + "api/site/v1/profile";
        authServletURI = baseURI + "authorization";
    }

    public Session getSession(ConfidentialClient cc, ResourceOwner ro) throws Exception {
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");

        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(cc)
                .baseURI(authServletURI)
                .scopes(scopes)
                .email(ro.getEmail())
                .build();

        String nextUri = baseURI + "profile";
        return postAuthorizationForm.getSessionForProfile(props, nextUri);
    }

    @Test
    public void putWhenNoSessionShouldBeUnauthorized() throws Exception {
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        Session session = getSession(cc, ro);

        Profile profile = ModelFactory.makeProfile(ro.getId());
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        byte[] payload = om.writerFor(Profile.class).writeValueAsBytes(profile);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePut(servletURI)
                .setBody(payload)

                .setHeader("Content-Type", "application/json; charset=utf-8;")
                .setHeader("Accept", "application/json; charset=utf-8;")
                .setHeader("X-CSRF", session.getCsrfToken())
                .setCookies(Collections.singletonList(session.getCsrf()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_UNAUTHORIZED));

        assertThat(response.getResponseBody(), is(""));
    }

    @Test
    public void putWhenNoCsrfShouldBeForbidden() throws Exception {
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        Session session = getSession(cc, ro);

        Profile profile = ModelFactory.makeProfile(ro.getId());
        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        byte[] payload = om.writerFor(Profile.class).writeValueAsBytes(profile);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePut(servletURI)
                .setBody(payload)
                .setHeader("Content-Type", "application/json; charset=utf-8;")
                .setHeader("Accept", "application/json; charset=utf-8;")
                .setCookies(Collections.singletonList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_FORBIDDEN));

        assertThat(response.getResponseBody(), is(""));
    }

    @Test
    public void putShouldBeOk() throws Exception {
        ConfidentialClient cc = loadOpenIdConfidentialClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        Session session = getSession(cc, ro);

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(session.getCsrf());
        cookies.add(session.getSession());

        Profile profile = ModelFactory.makeProfile(ro.getId(), ro.getProfile().getId());

        Name familyName = new Name();
        familyName.setProfileId(profile.getId());
        familyName.setName("Kenobi");
        profile.setFamilyName(familyName);

        Name givenName = new Name();
        givenName.setProfileId(profile.getId());
        givenName.setName("Obi-wan");
        profile.setGivenName(givenName);

        profile.setEmail(ro.getEmail());

        AppConfig config = new AppConfig();
        ObjectMapper om = config.objectMapper();
        byte[] payload = om.writerFor(Profile.class).writeValueAsBytes(profile);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePut(servletURI)
                .setBody(payload)
                .setHeader("Content-Type", "application/json;charset=UTF-8")
                .setHeader("Accept", "application/json;charset=UTF-8")
                .setHeader("X-CSRF", session.getCsrfToken())
                .setCookies(cookies)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));

        // should have a body.
        Profile actual = om.readerFor(Profile.class).readValue(response.getResponseBody());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(profile.getId()));
        assertThat(actual.getResourceOwnerId(), is(profile.getResourceOwnerId()));
        assertThat(actual.getEmail(), is(profile.getEmail()));

        assertTrue(actual.getName().isPresent());
        assertThat(actual.getName().get(), is(profile.getName().get()));

        assertFalse(actual.getMiddleName().isPresent());

        assertTrue(actual.getPreferredUserName().isPresent());
        assertThat(actual.getPreferredUserName().get(), is(profile.getPreferredUserName().get()));

        assertTrue(actual.getProfile().isPresent());
        assertThat(actual.getProfile().get(), is(profile.getProfile().get()));

        assertTrue(actual.getPicture().isPresent());
        assertThat(actual.getPicture().get(), is(profile.getPicture().get()));

        assertTrue(actual.getWebsite().isPresent());
        assertThat(actual.getWebsite().get(), is(profile.getWebsite().get()));

        assertTrue(actual.getGender().isPresent());
        assertThat(actual.getGender().get(), is(profile.getGender().get().toLowerCase()));

        assertFalse(actual.getBirthDate().isPresent());
        assertFalse(actual.getZoneInfo().isPresent());
        assertFalse(actual.getLocale().isPresent());
        assertFalse(actual.getPhoneNumber().isPresent());

        assertThat(actual.getGivenName(), is(notNullValue()));
        assertThat(actual.getGivenName().getId(), is(notNullValue()));
        assertThat(actual.getGivenName().getProfileId(), is(profile.getId()));
        assertThat(actual.getGivenName().getName(), is(givenName.getName()));

        assertThat(actual.getFamilyName(), is(notNullValue()));
        assertThat(actual.getFamilyName().getId(), is(notNullValue()));
        assertThat(actual.getFamilyName().getProfileId(), is(profile.getId()));
        assertThat(actual.getFamilyName().getName(), is(familyName.getName()));
    }
}