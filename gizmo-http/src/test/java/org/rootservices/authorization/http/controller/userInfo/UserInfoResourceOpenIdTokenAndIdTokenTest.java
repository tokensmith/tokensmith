package org.rootservices.authorization.http.controller.userInfo;

import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.FormFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.publik.LoadOpenIdPublicClient;
import helpers.fixture.persistence.http.GetSessionAndCsrfToken;
import helpers.fixture.persistence.http.Session;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadOpenIdResourceOwner;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.authorization.openId.identity.entity.IdToken;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.jws.verifier.VerifySignature;
import org.rootservices.jwt.serialization.JwtSerde;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 12/29/16.
 */
@Category(ServletContainerTest.class)
public class UserInfoResourceOpenIdTokenAndIdTokenTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static String authServletURI;

    private static LoadOpenIdPublicClient loadOpenIdPublicClientWithScopes;
    private static LoadOpenIdResourceOwner loadOpenIdResourceOwner;
    private static GetSessionAndCsrfToken getSessionAndCsrfToken;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;

    @BeforeClass
    public static void beforeClass() {
        servletURI = baseURI + "api/v1/userinfo";
        authServletURI = baseURI + "authorization";

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        ApplicationContext ac = IntegrationTestSuite.getContext();
        loadOpenIdPublicClientWithScopes = ac.getBean(LoadOpenIdPublicClient.class);
        loadOpenIdResourceOwner = ac.getBean(LoadOpenIdResourceOwner.class);
        getSessionAndCsrfToken = factoryForPersistence.makeGetSessionAndCsrfToken();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
    }

    public String makeToken(Client client, ResourceOwner ro, List<String> scopes) throws Exception {

        String authServletUriWithParams = this.authServletURI +
                "?client_id=" + client.getId().toString() +
                "&response_type=token id_token" +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectURI().toString(), StandardCharsets.UTF_8.name()) +
                "&nonce=some-nonce" +
                "&scope=";

        for(String scope: scopes) {
            authServletUriWithParams += URLEncoder.encode(scope + " ", StandardCharsets.UTF_8.name());
        }

        Session session = getSessionAndCsrfToken.run(authServletUriWithParams);
        List<Param> postData = FormFactory.makeLoginForm(ro.getEmail(), session.getCsrfToken());

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(authServletUriWithParams)
                .setFormParams(postData)
                .setCookies(Arrays.asList(session.getSession()))
                .execute();

        Response response = f.get();

        URI location = new URI(response.getHeader("location"));
        QueryStringToMap queryStringToMap = new QueryStringToMap();
        Map<String, List<String>> params = queryStringToMap.run(
                Optional.of(location.getQuery())
        );

        return params.get("access_token").get(0);
    }

    @Test
    public void getShouldBeOk() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        Client client = loadOpenIdPublicClientWithScopes.run();
        ResourceOwner ro = loadOpenIdResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");
        String token = makeToken(client, ro, scopes);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader(Header.ACCEPT.getValue(), ContentType.JWT.getValue())
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader(Header.AUTH.getValue(), "Bearer " + token)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(response.getContentType(), is(ContentType.JWT_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

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
        assertThat(claims.getAudience().get(0), is(client.getId().toString()));
        assertThat(claims.getExpirationTime().isPresent(), is(true));
        assertThat(claims.getIssuedAt().isPresent(), is(true));
        assertThat(claims.getAuthenticationTime(), is(notNullValue()));
    }
}
