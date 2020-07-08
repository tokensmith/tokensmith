package net.tokensmith.authorization.http.controller.userInfo;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import helpers.category.ServletContainerTest;
import helpers.fixture.EntityFactory;
import helpers.fixture.persistence.FactoryForPersistence;
import helpers.fixture.persistence.client.confidential.LoadConfClientCodeResponseType;
import helpers.fixture.persistence.http.PostAuthorizationForm;
import helpers.fixture.persistence.http.PostTokenCodeGrant;
import helpers.fixture.persistence.http.PostTokenRefreshGrant;
import helpers.fixture.persistence.db.GetOrCreateRSAPrivateKey;
import helpers.fixture.persistence.db.LoadResourceOwner;
import helpers.fixture.persistence.http.input.AuthEndpointProps;
import helpers.fixture.persistence.http.input.AuthEndpointPropsBuilder;
import helpers.suite.IntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.authorization.http.response.OpenIdToken;
import net.tokensmith.authorization.openId.identity.entity.IdToken;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.repo.TokenRepository;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.KeyType;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tommackenzie on 12/29/16.
 */
@Category(ServletContainerTest.class)
public class UserInfoResourceOAuth2RefreshTest {
    protected static String baseURI = String.valueOf(IntegrationTestSuite.getServer().getURI());
    protected static String servletURI;
    protected static String authServletURI;
    protected static String tokenServletURI;

    private static LoadResourceOwner loadResourceOwner;
    private static LoadConfClientCodeResponseType loadConfClientCodeResponseType;
    private static PostAuthorizationForm postAuthorizationForm;
    private static PostTokenCodeGrant postTokenCodeGrant;
    private static HashToken hashToken;
    private static TokenRepository tokenRepository;
    private static PostTokenRefreshGrant postTokenRefreshGrant;
    private static GetOrCreateRSAPrivateKey getOrCreateRSAPrivateKey;

    @BeforeClass
    public static void beforeClass() {
        servletURI = baseURI + "api/public/v1/userinfo";
        authServletURI = baseURI + "authorization";
        tokenServletURI = baseURI + "api/public/v1/token";

        FactoryForPersistence factoryForPersistence = new FactoryForPersistence(
                IntegrationTestSuite.getContext()
        );

        ApplicationContext ac = IntegrationTestSuite.getContext();
        loadConfClientCodeResponseType = ac.getBean(LoadConfClientCodeResponseType.class);
        loadResourceOwner = ac.getBean(LoadResourceOwner.class);
        postAuthorizationForm = factoryForPersistence.makePostAuthorizationForm();
        postTokenCodeGrant = factoryForPersistence.makePostTokenCodeGrant();
        hashToken = ac.getBean(HashToken.class);
        tokenRepository = ac.getBean(TokenRepository.class);
        postTokenRefreshGrant = factoryForPersistence.makePostTokenRefreshGrant();
        getOrCreateRSAPrivateKey = factoryForPersistence.getOrCreateRSAPrivateKey();
    }

    public String makeToken(ConfidentialClient cc, ResourceOwner ro, List<String> scopes) throws Exception {
        AuthEndpointProps props = new AuthEndpointPropsBuilder()
                .confidentialClient(cc)
                .baseURI(authServletURI)
                .scopes(scopes)
                .email(ro.getEmail())
                .build();

        String authorizationCode = postAuthorizationForm.run(props);
        OpenIdToken token = postTokenCodeGrant.run(cc, tokenServletURI, authorizationCode);
        expireAccessToken(token.getAccessToken());
        OpenIdToken tokenFromRefreshToken = postTokenRefreshGrant.run(cc, tokenServletURI, token.getRefreshToken());
        return tokenFromRefreshToken.getAccessToken();
    }

    public void expireAccessToken(String accessToken) {
        String hashedAccessToken = hashToken.run(accessToken);
        tokenRepository.updateExpiresAtByAccessToken(OffsetDateTime.now().minusDays(1), hashedAccessToken);
    }

    @Test
    public void getWhenNoProfileShouldReturn200() throws Exception {
        RSAPrivateKey key = getOrCreateRSAPrivateKey.run(2048);
        ConfidentialClient cc = loadConfClientCodeResponseType.run();
        ResourceOwner ro = loadResourceOwner.run();

        List<String> scopes = new ArrayList<>();
        scopes.add("profile");
        scopes.add("email");
        String token = makeToken(cc, ro, scopes);

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(servletURI)
                .setHeader("Accept", "application/jwt")
                .setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setHeader("Authorization", "Bearer " + token)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(response.getContentType(), is(ContentType.JWT_UTF_8.getValue()));
        assertThat(response.getHeader("Cache-Control"), is("no-store"));
        assertThat(response.getHeader("Pragma"), is("no-cache"));

        // verify id token
        JwtAppFactory appFactory = new JwtAppFactory();
        JwtSerde jwtSerde = appFactory.jwtSerde();

        JsonWebToken<IdToken> jwt = jwtSerde.stringToJwt(response.getResponseBody(), IdToken.class);

        RSAPublicKey publicKey = new RSAPublicKey(
                Optional.of(key.getId().toString()),
                Use.SIGNATURE,
                key.getModulus(),
                key.getPublicExponent()
        );

        VerifySignature verifySignature = appFactory.verifySignature(jwt.getHeader().getAlgorithm(), publicKey);
        Boolean signatureVerified = verifySignature.run(jwt);

        assertThat(signatureVerified, is(true));
        // email claims
        IdToken claims = jwt.getClaims();
        assertThat(claims.getEmail().isPresent(), is(true));
        assertThat(claims.getEmail().get(), is(ro.getEmail()));
        assertThat(claims.getEmailVerified().isPresent(), is(true));
        assertThat(claims.getEmailVerified().get(), is(false));

        // profile claims should be empty.
        assertThat(claims.getLastName().isPresent(), is(false));
        assertThat(claims.getFirstName().isPresent(), is(false));
        assertThat(claims.getMiddleName().isPresent(), is(false));
        assertThat(claims.getNickName().isPresent(), is(false));
        assertThat(claims.getPreferredUsername().isPresent(), is(false));
        assertThat(claims.getProfile().isPresent(), is(false));
        assertThat(claims.getPicture().isPresent(), is(false));
        assertThat(claims.getWebsite().isPresent(), is(false));
        assertThat(claims.getGender().isPresent(), is(false));
        assertThat(claims.getBirthdate().isPresent(), is(false));
        assertThat(claims.getZoneInfo().isPresent(), is(false));
        assertThat(claims.getLocale().isPresent(), is(false));
        assertThat(claims.getUpdatedAt().isPresent(), is(false));

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
