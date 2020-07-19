package net.tokensmith.authorization.http.controller.resource;

import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.db.LoadNonce;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.authorization.security.ciphers.IsTextEqualToHash;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.repo.NonceRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.apache.commons.httpclient.HttpStatus;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@Category(ServletContainerTest.class)
public class UpdatePasswordResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    protected static RandomString randomString;
    protected static LoadNonce loadNonce;
    protected static HashToken hashToken;
    protected static NonceRepository nonceRepository;
    protected static ResourceOwnerRepository resourceOwnerRepository;
    protected static IsTextEqualToHash isTextEqualToHash;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;

    @BeforeClass
    public static void beforeClass() {
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        randomString = IntegrationTestSuite.getContext().getBean(RandomString.class);
        loadNonce = IntegrationTestSuite.getContext().getBean(LoadNonce.class);
        hashToken = IntegrationTestSuite.getContext().getBean(HashToken.class);
        nonceRepository = IntegrationTestSuite.getContext().getBean(NonceRepository.class);
        resourceOwnerRepository = IntegrationTestSuite.getContext().getBean(ResourceOwnerRepository.class);
        isTextEqualToHash = IntegrationTestSuite.getContext().getBean(IsTextEqualToHash.class);
        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();

        servletURI = baseURI + "update-password";
    }

    @Test
    public void getShouldReturn200() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashToken.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=" + jwt)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void getWhenNonceIsEmptyShouldReturn400() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getResponseBody().contains("data-status=\"error\""), is(true));
    }

    @Test
    public void getWhenNonceIsNotAJwtShouldReturn400() throws Exception {
        String notAJwt= "not-a-jwt";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=" + notAJwt)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getResponseBody().contains("data-status=\"error\""), is(true));
    }

    @Test
    public void postShouldReturn200() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashToken.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce);

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI + "?nonce=" + jwt);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        String password = "password123";

        // before the test runs. make sure the new password does not equal the existing.
        Boolean passwordsMatch = isTextEqualToHash.run(password, ro.getPassword());
        assertFalse(passwordsMatch);

        List<Param> postData = FormFactory.makeUpdatePasswordForm(password, password, session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI + "?nonce=" + jwt)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getCsrf()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));

        // re-fetch resource owner to ensure the password was updated.
        ro = resourceOwnerRepository.getByEmail(ro.getEmail());
        Boolean actual = isTextEqualToHash.run(password, ro.getPassword());
        assertThat(actual, is(true));
    }

    @Test
    public void postWhenPasswordEmptyShouldReturn400() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashToken.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce);

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI + "?nonce=" + jwt);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeUpdatePasswordForm("", "password123", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI + "?nonce=" + jwt)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getCsrf()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getResponseBody().contains("data-status=\"form-error\""), is(true));
    }

    @Test
    public void postWhenNonceNotFoundShouldReturn404() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI + "?nonce=" + jwt);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        List<Param> postData = FormFactory.makeUpdatePasswordForm("password123", "password123", session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI + "?nonce=" + jwt)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getCsrf()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
        assertThat(response.getResponseBody().contains("data-status=\"link-expired\""), is(true));
    }

    @Test
    public void postWhenNonceInvalidShouldReturn400() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashToken.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce);

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI + "?nonce=" + jwt);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        String password = "password123";

        // before the test runs. make sure the new password does not equal the existing.
        Boolean passwordsMatch = isTextEqualToHash.run(password, ro.getPassword());
        assertFalse(passwordsMatch);

        List<Param> postData = FormFactory.makeUpdatePasswordForm(password, password, session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI + "?nonce=not-a-jwt")
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getCsrf()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getResponseBody().contains("data-status=\"error\""), is(true));
    }
}