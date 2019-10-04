package org.rootservices.authorization.http.controller;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.persistence.db.LoadNonce;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.RandomString;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.authorization.security.entity.NonceClaim;
import org.rootservices.jwt.builder.compact.UnsecureCompactBuilder;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class WelcomeResourceTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    protected static RandomString randomString;
    protected static LoadNonce loadNonce;
    protected static HashTextStaticSalt hashTextStaticSalt;
    protected static NonceRepository nonceRepository;
    protected static ResourceOwnerRepository resourceOwnerRepository;

    @BeforeClass
    public static void beforeClass() {
        loadOpenIdResourceOwner = IntegrationTestSuite.getContext().getBean(LoadOpenIdResourceOwner.class);
        randomString = IntegrationTestSuite.getContext().getBean(RandomString.class);
        loadNonce = IntegrationTestSuite.getContext().getBean(LoadNonce.class);
        hashTextStaticSalt = IntegrationTestSuite.getContext().getBean(HashTextStaticSalt.class);
        nonceRepository = IntegrationTestSuite.getContext().getBean(NonceRepository.class);
        resourceOwnerRepository = IntegrationTestSuite.getContext().getBean(ResourceOwnerRepository.class);
        servletURI = baseURI + "welcome";
    }

    @Test
    public void getShouldSpendNonceAndReturn200() throws Exception {
        String plainTextNonce = randomString.run();


        NonceClaim nonceClaim = new NonceClaim();
        nonceClaim.setNonce(plainTextNonce);
        UnsecureCompactBuilder compactBuilder = new UnsecureCompactBuilder();
        String jwt = compactBuilder.claims(nonceClaim).build().toString();

        String hashedNonce = hashTextStaticSalt.run(plainTextNonce);
        ResourceOwner ro = loadOpenIdResourceOwner.run();
        loadNonce.welcome(ro, hashedNonce.getBytes());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=" + jwt)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));

        // make sure the nonce is spent
        Nonce nonce = nonceRepository.getByNonce(hashedNonce);
        assertThat(nonce, is(notNullValue()));
        assertThat(nonce.getNonceType().getName(), is("welcome"));
        assertThat(nonce.getSpent(), is(true));

        // make sure the ro email is validated.
        ro = resourceOwnerRepository.getById(ro.getId());
        assertThat(ro, is(notNullValue()));
        assertThat(ro.isEmailVerified(), is(true));
    }

    @Test
    public void getWhenNonceIsEmptyShouldReturn400() throws Exception {

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI + "?nonce=")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

}
