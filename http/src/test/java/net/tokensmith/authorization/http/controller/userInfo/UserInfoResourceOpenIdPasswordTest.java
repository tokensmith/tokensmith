package net.tokensmith.authorization.http.controller.userInfo;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadOpenIdConfClientPasswordResponseType;
import helpers.fixture.persistence.http.PostTokenPasswordGrant;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.http.response.OpenIdToken;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.router.GetServletURI;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 12/29/16.
 */
@Category(ServletContainerTest.class)
public class UserInfoResourceOpenIdPasswordTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static GetServletURI getServletURI;
    protected static String servletURI;
    protected static String tokenServletURI;

    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static LoadOpenIdConfClientPasswordResponseType loadOpenIdConfClientPasswordResponseType;
    private static PostTokenPasswordGrant postTokenPasswordGrant;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;

    @BeforeClass
    public static void beforeClass() {
        getServletURI = new GetServletURI();
        servletURI = baseURI + "api/public/v1/userinfo";
        tokenServletURI = baseURI + "api/public/v1/token";

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        ApplicationContext ac = IntegrationTestSuite.getContext();
        loadOpenIdConfClientPasswordResponseType = ac.getBean(LoadOpenIdConfClientPasswordResponseType.class);
        loadOpenIdResourceOwner = ac.getBean(LoadOpenIdResourceOwner.class);
        postTokenPasswordGrant = factoryForPersistence.postPasswordGrant();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
    }

    @Test
    public void getShouldBeOk() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ConfidentialClient cc = loadOpenIdConfClientPasswordResponseType.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        OpenIdToken token = postTokenPasswordGrant.post(
                ro.getEmail(),
                "password",
                "email",
                cc.getClient().getId().toString(),
                "password",
                tokenServletURI
        );

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.ACCEPT.getValue(), ContentType.JWT_UTF_8.getValue())
                .setHeader(Header.AUTH.getValue(), "Bearer " + new String(token.getAccessToken()))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(response.getContentType(), is(ContentType.JWT_UTF_8.getValue()));
        assertThat(response.getHeader(Header.CACHE_CONTROL.getValue()), is(HeaderValue.NO_STORE.getValue()));
        assertThat(response.getHeader(Header.PRAGMA.getValue()), is(HeaderValue.NO_CACHE.getValue()));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken jwt = jwtSerde.stringToJwt(response.getResponseBody(), IdToken.class);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                KeyType.RSA,
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));

        IdToken claims = (IdToken) jwt.getClaims();
        assertThat(claims.getEmail().isPresent(), is(true));
        assertThat(claims.getEmail().get(), is(ro.getEmail()));
        assertThat(claims.getEmailVerified().isPresent(), is(true));
        assertThat(claims.getEmailVerified().get(), is(false));

        // required claims.
        assertThat(claims.getIssuer().isPresent(), is(true));
        assertThat(claims.getIssuer().get(), is(EntityFactory.ISSUER));
        assertThat(claims.getAudience(), is(notNullValue()));
        assertThat(claims.getAudience().size(), is(1));
        assertThat(claims.getAudience().get(0), is(cc.getClient().getId().toString()));
        assertThat(claims.getExpirationTime().isPresent(), is(true));
        assertThat(claims.getIssuedAt().isPresent(), is(true));
        assertThat(claims.getAuthenticationTime(), is(notNullValue()));
    }
}
