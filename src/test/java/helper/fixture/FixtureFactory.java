package helper.fixture;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenClaims;
import org.rootservices.authorization.openId.grant.redirect.implicit.authorization.request.entity.OpenIdImplicitAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdInputParams;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.*;
import org.rootservices.config.AppConfig;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwk.Use;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class FixtureFactory {

    public static String PLAIN_TEXT_PASSWORD = "password";
    public static String SECURE_REDIRECT_URI = "https://rootservices.org";
    public static String REDIRECT_URI = "http://www.rootservices.org";

    public static URI makeSecureRedirectUri() throws URISyntaxException {
        return new URI(REDIRECT_URI);
    }

    public static Client makeTokenClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = makeResponseType();
        rt.setName("TOKEN");

        List<ResponseType> responseTypes = new ArrayList<>();
        responseTypes.add(rt);
        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
        return client;
    }

    public static Client makeCodeClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();

        ResponseType rt = makeResponseType();
        rt.setName("CODE");

        List<ResponseType> responseTypes = new ArrayList<>();
        responseTypes.add(rt);

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
        return client;
    }

    public static Client makeCodeClientWithOpenIdScopes() throws URISyntaxException {
        Client client = makeCodeClientWithScopes();
        client.setScopes(makeOpenIdScopes());
        return client;
    }

    public static Client makePasswordClientWithOpenIdScopes() throws URISyntaxException {

        Client client = makePasswordClientWithScopes();
        client.getScopes().add(makeScope("openid"));
        return client;
    }

    public static Client makePasswordClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();

        ResponseType rt = makeResponseType();
        rt.setName("PASSWORD");

        List<ResponseType> responseTypes = new ArrayList<>();
        responseTypes.add(rt);

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
        return client;
    }

    public static ConfidentialClient makeConfidentialClient(Client client) {
        ConfidentialClient confidentialClient = new ConfidentialClient();
        confidentialClient.setId(UUID.randomUUID());
        confidentialClient.setClient(client);
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String password = textHasher.run(PLAIN_TEXT_PASSWORD);
        confidentialClient.setPassword(password.getBytes());

        return confidentialClient;
    }

    public static Client makeTokenClientWithOpenIdScopes() throws URISyntaxException {
        Client client = makeTokenClientWithScopes();
        client.setScopes(makeOpenIdScopes());
        return client;
    }

    public static List<Scope> makeScopes() {
        List<Scope> scopes = new ArrayList<>();

        Scope scope = makeScope();
        scopes.add(scope);
        return scopes;
    }

    public static Scope makeScope() {
        Scope scope = new Scope();
        scope.setId(UUID.randomUUID());
        scope.setName("profile");

        return scope;
    }

    public static Scope makeScope(String name) {
        Scope scope = new Scope();
        scope.setId(UUID.randomUUID());
        scope.setName(name);

        return scope;
    }

    public static List<ResponseType> makeResponseTypes() {
        List<ResponseType> responseTypes = new ArrayList<>();
        responseTypes.add(makeResponseType());
        return responseTypes;
    }

    public static ResponseType makeResponseType() {
        ResponseType rt = new ResponseType();
        rt.setId(UUID.randomUUID());
        return rt;
    }
    public static List<Scope> makeOpenIdScopes() {
        List<Scope> scopes = new ArrayList<>();
        Scope scope = new Scope();
        scope.setId(UUID.randomUUID());
        scope.setName("openid");
        scopes.add(scope);
        return scopes;
    }

    public static String makeRandomEmail() {
        return "test-" + UUID.randomUUID().toString() + "@rootservices.org";
    }

    public static ResourceOwner makeResourceOwner() {
        ResourceOwner ro = new ResourceOwner();
        ro.setId(UUID.randomUUID());

        ro.setEmail(makeRandomEmail());
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run(PLAIN_TEXT_PASSWORD);
        ro.setPassword(hashedPassword.getBytes());
        ro.setEmailVerified(false);

        return ro;
    }

    public static Profile makeProfile(UUID resourceOwnerId) throws URISyntaxException {
        Profile profile = new Profile();

        profile.setId(UUID.randomUUID());
        profile.setResourceOwnerId(resourceOwnerId);
        profile.setName(Optional.of("Obi-Wan Kenobi"));
        profile.setMiddleName(Optional.empty());
        profile.setNickName(Optional.of("Ben"));
        profile.setPreferredUserName(Optional.of("Ben Kenobi"));
        profile.setProfile(Optional.of(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));
        profile.setPicture(Optional.of(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));
        profile.setWebsite(Optional.of(new URI("http://starwars.wikia.com")));
        profile.setGender(Optional.of(Gender.MALE));
        profile.setBirthDate(Optional.empty());
        profile.setZoneInfo(Optional.empty());
        profile.setLocale(Optional.empty());
        profile.setPhoneNumber(Optional.empty());
        profile.setPhoneNumberVerified(false);


        return profile;
    }

    public static Address makeAddress(UUID profileId) {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setProfileId(profileId);
        address.setStreetAddress("123 Jedi High Council Rd.");
        address.setStreetAddress2(Optional.empty());
        address.setLocality("Coruscant");
        address.setPostalCode("12345");
        address.setRegion("Coruscant");
        address.setCountry("Old Republic");

        return address;
    }

    public static GivenName makeGivenName(UUID profileId){
        GivenName givenName = new GivenName();
        givenName.setId(UUID.randomUUID());
        givenName.setResourceOwnerProfileId(profileId);
        givenName.setName("Obi-Wan");

        return givenName;
    }

    public static FamilyName makeFamilyName(UUID profileId){
        FamilyName familyName = new FamilyName();
        familyName.setId(UUID.randomUUID());
        familyName.setResourceOwnerProfileId(profileId);
        familyName.setName("Kenobi");

        return familyName;
    }

    public static AuthCode makeAuthCode(AccessRequest accessRequest, boolean isRevoked, String plainTextAuthCode) {
        AuthCode authCode = new AuthCode();
        authCode.setId(UUID.randomUUID());

        AppConfig config = new AppConfig();
        HashTextStaticSalt textHasher = new HashTextStaticSaltImpl(config.salt());
        String hashedCode = textHasher.run(plainTextAuthCode);

        authCode.setCode(hashedCode.getBytes());
        authCode.setRevoked(isRevoked);
        authCode.setAccessRequest(accessRequest);
        authCode.setExpiresAt(OffsetDateTime.now().plusMinutes(3));

        return authCode;
    }

    public static AccessRequest makeAccessRequest(UUID resourceOwnerId, UUID clientUUID) throws URISyntaxException {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setId(UUID.randomUUID());
        accessRequest.setResourceOwnerId(resourceOwnerId);
        accessRequest.setClientId(clientUUID);
        accessRequest.setRedirectURI(Optional.of(new URI(SECURE_REDIRECT_URI)));

        return accessRequest;
    }

    public static Token makeOAuthToken(String accessToken, UUID clientId) {
        AppConfig config = new AppConfig();
        HashTextStaticSalt textHasher = new HashTextStaticSaltImpl(config.salt());
        String hashedAccessToken = textHasher.run(accessToken);

        Token token = new Token();
        token.setId(UUID.randomUUID());
        token.setToken(hashedAccessToken.getBytes());
        token.setExpiresAt(OffsetDateTime.now());
        token.setGrantType(GrantType.AUTHORIZATION_CODE);
        token.setClientId(clientId);
        token.setTokenScopes(new ArrayList<>());
        token.setSecondsToExpiration(3600L);

        TokenScope ts1 = new TokenScope();
        ts1.setId(UUID.randomUUID());
        Scope profile = new Scope();
        profile.setId(UUID.randomUUID());
        profile.setName("profile");
        ts1.setScope(profile);

        token.getTokenScopes().add(ts1);

        return token;
    }

    public static Token makeOpenIdToken(String accessToken, UUID clientId) {
        Token token = makeOAuthToken(accessToken, clientId);

        TokenScope ts1 = new TokenScope();
        ts1.setId(UUID.randomUUID());
        Scope openid = new Scope();
        openid.setId(UUID.randomUUID());
        openid.setName("openid");
        ts1.setScope(openid);

        token.getTokenScopes().add(ts1);

        return token;
    }

    public static RefreshToken makeRefreshToken(String refreshAccessToken, Token token) {
        AppConfig config = new AppConfig();
        HashTextStaticSalt textHasher = new HashTextStaticSaltImpl(config.salt());
        String hashedRefreshAccessToken = textHasher.run(refreshAccessToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setTokenId(token.getId());
        refreshToken.setToken(token);
        refreshToken.setAccessToken(hashedRefreshAccessToken.getBytes());
        refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(1209600));
        refreshToken.setRevoked(false);
        return refreshToken;
    }

    public static ResourceOwnerToken makeResourceOwnerToken(String accessToken, UUID clientId) {

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setResourceOwner(makeResourceOwner());
        resourceOwnerToken.setToken(makeOpenIdToken(accessToken, clientId));
        return resourceOwnerToken;
    }

    public static InputParams makeInputParams(UUID clientId, String responseType, String scope) {
        InputParams input = new InputParams();
        input.setUserName(makeRandomEmail());
        input.setPlainTextPassword(PLAIN_TEXT_PASSWORD);

        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());
        input.setClientIds(clientIds);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(responseType);
        input.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add(scope.toString());
        input.setScopes(scopes);

        List<String> states = new ArrayList<>();
        input.setStates(states);

        return input;
    }

    public static OpenIdInputParams makeOpenIdInputParams(UUID clientId, String responseType) {
        OpenIdInputParams input = new OpenIdInputParams();
        input.setUserName(makeRandomEmail());
        input.setPlainTextPassword(PLAIN_TEXT_PASSWORD);

        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());
        input.setClientIds(clientIds);

        List<String> redirectUris = new ArrayList();
        redirectUris.add(SECURE_REDIRECT_URI);
        input.setRedirectUris(redirectUris);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(responseType);
        input.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add("openid profile");
        input.setScopes(scopes);

        List<String> states = new ArrayList<>();
        states.add("some-state");
        input.setStates(states);

        List<String> nonces = new ArrayList<>();
        nonces.add("some-nonce");
        input.setNonces(nonces);

        return input;
    }

    public static InputParams makeEmptyGrantInput() {
        InputParams input = new InputParams();
        input.setUserName(makeRandomEmail());
        input.setPlainTextPassword(PLAIN_TEXT_PASSWORD);

        List<String> clientIds = new ArrayList<>();
        input.setClientIds(clientIds);

        List<String> redirectUris = new ArrayList<>();
        input.setRedirectUris(redirectUris);

        List<String> responseTypes = new ArrayList<>();
        input.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        input.setScopes(scopes);

        List<String> states = new ArrayList<>();
        input.setStates(states);

        return input;
    }

    public static RSAPrivateKey makeRSAPrivateKey() {
        RSAPrivateKey rsaPrivateKey = new RSAPrivateKey();
        rsaPrivateKey.setId(UUID.randomUUID());
        rsaPrivateKey.setUse(PrivateKeyUse.SIGNATURE);
        rsaPrivateKey.setModulus(new BigInteger("1"));
        rsaPrivateKey.setPublicExponent(new BigInteger("2"));
        rsaPrivateKey.setPrivateExponent(new BigInteger("3"));
        rsaPrivateKey.setPrimeP(new BigInteger("4"));
        rsaPrivateKey.setPrimeQ(new BigInteger("5"));
        rsaPrivateKey.setPrimeExponentP(new BigInteger("6"));
        rsaPrivateKey.setPrimeExponentQ(new BigInteger("7"));
        rsaPrivateKey.setCrtCoefficient(new BigInteger("8"));
        rsaPrivateKey.setActive(true);

        return rsaPrivateKey;
    }

    public static RSAKeyPair makeRSAKeyPair() {
        return new RSAKeyPair(
                Optional.of("test-key-id"),
                KeyType.RSA,
                Use.SIGNATURE,
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4"),
                new BigInteger("5"),
                new BigInteger("6"),
                new BigInteger("7"),
                new BigInteger("8")
        );
    }

    public static List<AccessRequestScope> makeAccessRequestScopes() {
        List<AccessRequestScope> accessRequestScopes = new ArrayList<>();

        Scope openId = makeScope();
        openId.setName("openid");
        AccessRequestScope arsOpenId = makeAccessRequestScope();
        arsOpenId.setScope(openId);
        accessRequestScopes.add(arsOpenId);

        Scope profile = makeScope();
        AccessRequestScope arsProfile = makeAccessRequestScope();
        arsProfile.setScope(profile);
        accessRequestScopes.add(arsProfile);

        return accessRequestScopes;
    }

    public static AccessRequestScope makeAccessRequestScope() {
        AccessRequestScope accessRequestScope = new AccessRequestScope();
        accessRequestScope.setId(UUID.randomUUID());
        accessRequestScope.setCreatedAt(OffsetDateTime.now());

        return accessRequestScope;
    }

    public static OpenIdImplicitAuthRequest makeOpenIdImplicitAuthRequest(UUID clientId) throws URISyntaxException {
        OpenIdImplicitAuthRequest request = new OpenIdImplicitAuthRequest();
        request.setClientId(clientId);
        request.setRedirectURI(new URI(FixtureFactory.SECURE_REDIRECT_URI));
        request.setNonce("nonce");
        request.setState(Optional.of("state"));
        request.setScopes(new ArrayList<>());
        request.getScopes().add("openid");
        request.getScopes().add("profile");

        return request;
    }

    public static TokenClaims makeTokenClaims(List<String> audience) {
        TokenClaims tc = new TokenClaims();
        tc.setIssuer("https://sso.rootservices.org");
        tc.setAudience(audience);
        tc.setIssuedAt(OffsetDateTime.now().toEpochSecond());
        tc.setExpirationTime(OffsetDateTime.now().plusDays(1).toEpochSecond());
        tc.setAuthTime(OffsetDateTime.now().toEpochSecond());

        return tc;
    }

    public static Configuration makeConfiguration() {
        Configuration c = new Configuration();
        c.setId(UUID.randomUUID());
        c.setAccessTokenSize(32);
        c.setRefreshTokenSize(32);
        c.setAuthorizationCodeSize(32);

        // these are different so I can verify the right expiry is used in tests.
        c.setAccessTokenTokenSecondsToExpiry(3601L);
        c.setAccessTokenCodeSecondsToExpiry(3602L);
        c.setAccessTokenPasswordSecondsToExpiry(3603L);
        c.setAccessTokenRefreshSecondsToExpiry(3604L);
        c.setAccessTokenClientSecondsToExpiry(3605L);
        c.setAuthorizationCodeSecondsToExpiry(120L);
        c.setRefreshTokenSecondsToExpiry(1209600L);
        c.setCreatedAt(OffsetDateTime.now());
        c.setUpdatedAt(OffsetDateTime.now());
        return c;
    }

    public static TokenGraph makeTokenGraph(UUID clientId) {
        String plainTextToken = "plain-text-token";
        Token token = makeOpenIdToken(plainTextToken, clientId);
        token.setCreatedAt(OffsetDateTime.now());
        String refreshAccessToken = "refresh-token";

        TokenGraph tokenGraph = new TokenGraph(
                token,
                Optional.of(UUID.randomUUID()),
                plainTextToken,
                Optional.of(refreshAccessToken),
                Extension.IDENTITY
        );

        return tokenGraph;
    }

    public static TokenGraph makeImplicitTokenGraph(UUID clientId) {
        String plainTextToken = "plain-text-token";
        Token token = makeOpenIdToken(plainTextToken, clientId);
        token.setCreatedAt(OffsetDateTime.now());

        TokenGraph tokenGraph = new TokenGraph(
                token,
                Optional.empty(),
                plainTextToken,
                Optional.empty(),
                Extension.IDENTITY
        );

        return tokenGraph;
    }
}