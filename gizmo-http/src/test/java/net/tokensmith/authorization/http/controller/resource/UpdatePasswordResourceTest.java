package net.tokensmith.authorization.http.controller.resource;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.FormFactory;
import helpers.fixture.exception.GetCsrfException;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.db.LoadNonce;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.suite.IntegrationTestSuite;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.authorization.persistence.repository.NonceRepository;
import net.tokensmith.authorization.persistence.repository.ResourceOwnerRepository;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashTextStaticSalt;
import net.tokensmith.authorization.security.ciphers.IsTextEqualToHash;
import net.tokensmith.authorization.security.entity.NonceClaim;
import net.tokensmith.jwt.builder.compact.UnsecureCompactBuilder;


import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(ServletContainerTest.class)
public class UpdatePasswordResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    protected static RandomString randomString;
    protected static LoadNonce loadNonce;
    protected static HashTextStaticSalt hashTextStaticSalt;
    protected static NonceRepository nonceRepository;
    protected static ResourceOwnerRepository resourceOwnerRepository;
    protected static IsTextEqualToHash isTextEqualToHash;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;

    @BeforeClass
    public static void beforeClass() {
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        randomString = IntegrationTestSuite.getContext().getBean(RandomString.class);
        loadNonce = IntegrationTestSuite.getContext().getBean(LoadNonce.class);
        hashTextStaticSalt = IntegrationTestSuite.getContext().getBean(HashTextStaticSalt.class);
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

        String hashedNonce = hashTextStaticSalt.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce.getBytes());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=" + jwt)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void postShouldReturn200() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashTextStaticSalt.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce.getBytes());

        Session session = new Session();
        try {
            session = getSessionAndCsrfToken.run(servletURI + "?nonce=" + jwt);
        } catch (GetCsrfException e) {
            fail("CSRF error - status code: " + e.getStatusCode() + ", redirect location: " + e.getRedirectUri() + ", response body: " + e.getResponseBody());
        }

        String password = "password123";

        // before the test runs. make sure the new password does not equal the existing.
        Boolean passwordsMatch = isTextEqualToHash.run(password, new String(ro.getPassword(), "UTF-8"));
        assertFalse(passwordsMatch);

        List<Param> postData = FormFactory.makeUpdatePasswordForm(password, password, session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(servletURI + "?nonce=" + jwt)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getResponseBody().contains("error"), is(false));

        // re-fetch resource owner to ensure the password was updated.
        ro = resourceOwnerRepository.getByEmail(ro.getEmail());
        Boolean actual = isTextEqualToHash.run(password, new String(ro.getPassword(), "UTF-8"));
        assertThat(actual, is(true));
    }

    @Test
    public void postWhenPasswordEmptyShouldReturn400() throws Exception {
        String plainTextNonce = randomString.run();

        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashTextStaticSalt.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.resetPassword(ro, hashedNonce.getBytes());

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
                .setCookies(Arrays.asList(session.getSession()))
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
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
        assertThat(response.getResponseBody().contains("data-status=\"link-expired\""), is(true));
    }
}