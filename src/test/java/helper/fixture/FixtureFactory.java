package helper.fixture;

import org.rootservices.authorization.grant.code.protocol.authorization.response.AuthCodeInput;
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

    public static Client makeClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, rt, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
        return client;
    }

    public static Client makeClientWithOpenIdScopes() throws URISyntaxException {
        Client client = makeClientWithScopes();
        client.setScopes(makeOpenIdScopes());
        return client;
    }

    public static ConfidentialClient makeConfidentialClient(Client client) {
        ConfidentialClient confidentialClient = new ConfidentialClient();
        confidentialClient.setUuid(UUID.randomUUID());
        confidentialClient.setClient(client);
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String password = textHasher.run(PLAIN_TEXT_PASSWORD);
        confidentialClient.setPassword(password.getBytes());

        return confidentialClient;
    }

    public static List<Scope> makeScopes() {
        List<Scope> scopes = new ArrayList<>();

        Scope scope = makeScope();
        scopes.add(scope);
        return scopes;
    }

    public static Scope makeScope() {
        Scope scope = new Scope();
        scope.setUuid(UUID.randomUUID());
        scope.setName("profile");

        return scope;
    }

    public static List<Scope> makeOpenIdScopes() {
        List<Scope> scopes = new ArrayList<>();
        Scope scope = new Scope();
        scope.setUuid(UUID.randomUUID());
        scope.setName("openid");
        scopes.add(scope);
        return scopes;
    }

    public static String makeRandomEmail() {
        return "test-" + UUID.randomUUID().toString() + "@rootservices.org";
    }

    public static ResourceOwner makeResourceOwner() {
        ResourceOwner ro = new ResourceOwner();
        ro.setUuid(UUID.randomUUID());

        ro.setEmail(makeRandomEmail());
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run(PLAIN_TEXT_PASSWORD);
        ro.setPassword(hashedPassword.getBytes());

        return ro;
    }

    public static Profile makeProfile(ResourceOwner resourceOwner) throws URISyntaxException {
        Profile profile = new Profile();

        profile.setId(UUID.randomUUID());
        profile.setResourceOwner(resourceOwner);
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
        authCode.setUuid(UUID.randomUUID());
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
        accessRequest.setUuid(UUID.randomUUID());
        accessRequest.setResourceOwnerUUID(resourceOwnerId);
        accessRequest.setClientUUID(clientUUID);
        accessRequest.setRedirectURI(Optional.of(new URI(SECURE_REDIRECT_URI)));

        return accessRequest;
    }

    public static Token makeToken() {
        RandomString randomString = new RandomStringImpl();

        Token token = new Token();
        token.setUuid(UUID.randomUUID());
        token.setToken(randomString.run().getBytes());
        token.setExpiresAt(OffsetDateTime.now());
        token.setGrantType(GrantType.AUTHORIZATION_CODE);

        return token;
    }

    public static ResourceOwnerToken makeResourceOwnerToken() {
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setResourceOwner(makeResourceOwner());
        resourceOwnerToken.setToken(makeToken());
        return resourceOwnerToken;
    }

    public static AuthCodeInput makeAuthCodeInput(UUID clientId, ResponseType rt, String scope) {
        AuthCodeInput input = new AuthCodeInput();
        input.setUserName(makeRandomEmail());
        input.setPlainTextPassword(PLAIN_TEXT_PASSWORD);

        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId.toString());
        input.setClientIds(clientIds);

        List<String> responseTypes = new ArrayList<>();
        responseTypes.add(rt.toString());
        input.setResponseTypes(responseTypes);

        List<String> scopes = new ArrayList<>();
        scopes.add(scope.toString());
        input.setScopes(scopes);

        return input;
    }

    public static RSAPrivateKey makeRSAPrivateKey() {
        RSAPrivateKey rsaPrivateKey = new RSAPrivateKey();
        rsaPrivateKey.setUuid(UUID.randomUUID());
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
}
