package helpers.fixture;


import net.tokensmith.authorization.register.request.UserInfo;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSaltImpl;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.entity.Gender;
import net.tokensmith.repository.entity.Name;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.ResponseType;
import net.tokensmith.repository.entity.Scope;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class EntityFactory {
    public static String PLAIN_TEXT_PASSWORD = "password";
    public static String SECURE_REDIRECT_URI = "https://tokensmith.net";
    public static String ISSUER = "https://sso.tokensmith.net";

    public static Optional<String> anyOptionalString() {
        Optional<String> any = Mockito.any();
        return any;
    }

    // used to eliminate mockito warnings.
    public static Optional<URI> anyOptionalURI() {
        Optional<URI> any = Mockito.any();
        return any;
    }

    public static ConfidentialClient makeConfidentialClient(Client client) {
        ConfidentialClient confidentialClient = new ConfidentialClient();
        confidentialClient.setId(UUID.randomUUID());
        confidentialClient.setClient(client);
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run("password");
        confidentialClient.setPassword(hashedPassword);

        return confidentialClient;
    }

    public static Client makeClientWithCodeResponseTypeAndScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();

        List<ResponseType> responseTypes = makeResponseTypes("CODE");

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes("profile");
        scopes.add(makeScope("email"));

        client.setScopes(scopes);
        return client;
    }

    public static Client makeClientWithTokenResponseTypeAndScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        List<ResponseType> responseTypes = makeResponseTypes("TOKEN");
        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes("profile");
        scopes.add(makeScope("email"));
        client.setScopes(scopes);
        return client;
    }

    public static Client makeOpenIdCodeClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        List<ResponseType> responseTypes = makeResponseTypes("CODE");
        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = new ArrayList<>();
        scopes.add(makeScope("openid"));
        scopes.add(makeScope("email"));
        scopes.add(makeScope("profile"));
        client.setScopes(scopes);
        return client;
    }

    public static Client makeOpenIdPublicClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        List<ResponseType> responseTypes = makeResponseTypes("TOKEN");
        ResponseType idToken = makeResponseType();
        idToken.setName("ID_TOKEN");
        responseTypes.add(idToken);

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = new ArrayList<>();
        scopes.add(makeScope("openid"));
        scopes.add(makeScope("email"));
        client.setScopes(scopes);
        return client;
    }

    public static Client makeOpenIdPublicClientIdTokenOnlyWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        List<ResponseType> responseTypes = makeResponseTypes("ID_TOKEN");

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = new ArrayList<>();
        scopes.add(makeScope("openid"));
        scopes.add(makeScope("email"));
        client.setScopes(scopes);
        return client;
    }

    public static Client makeClientWithPasswordResponseTypeAndScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();

        List<ResponseType> responseTypes = makeResponseTypes("PASSWORD");

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes("profile");
        scopes.add(makeScope("email"));
        client.setScopes(scopes);
        return client;
    }

    public static Client makeOpenIdClientWithPasswordResponseTypeAndScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();

        List<ResponseType> responseTypes = makeResponseTypes("PASSWORD");

        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, responseTypes, redirectUri);
        List<Scope> scopes = makeScopes("openid");
        client.setScopes(scopes);
        client.getScopes().add(makeScope("email"));

        return client;
    }

    public static List<Scope> makeScopes(String scopeName) {
        List<Scope> scopes = new ArrayList<>();
        Scope scope = makeScope(scopeName);
        scopes.add(scope);
        return scopes;
    }

    public static Scope makeScope(String scopeName) {
        Scope scope = new Scope();
        scope.setId(UUID.randomUUID());
        scope.setName(scopeName);
        return scope;
    }

    public static List<ResponseType> makeResponseTypes(String name) {
        List<ResponseType> responseTypes = new ArrayList<>();
        ResponseType rt = makeResponseType();
        rt.setName(name);
        responseTypes.add(rt);

        return responseTypes;
    }

    public static ResponseType makeResponseType() {
        ResponseType rt = new ResponseType();
        rt.setId(UUID.randomUUID());
        return rt;
    }

    public static UserInfo makeFullUserInfo() throws Exception {

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("obi-wan-kenobi-" + UUID.randomUUID() + "@tokensmith.net");
        userInfo.setPassword("password");
        userInfo.setName(Optional.of("Obi-Wan Kenobi"));
        userInfo.setFamilyName(Optional.of("Kenobi"));
        userInfo.setGivenName(Optional.of("Obi-Wan"));
        userInfo.setMiddleName(Optional.of("Wan"));
        userInfo.setNickName(Optional.of("Ben"));
        userInfo.setPreferredUserName(Optional.of("Ben Kenobi"));
        userInfo.setProfile(Optional.of(new URI("http://starwars.wikia.com/wiki/Obi-Wan_Kenobi")));
        userInfo.setPicture(Optional.of(new URI("http://vignette1.wikia.nocookie.net/starwars/images/2/25/Kenobi_Maul_clash.png/revision/latest?cb=20130120033039")));
        userInfo.setWebsite(Optional.of(new URI("http://starwars.wikia.com")));
        userInfo.setGender(Optional.of("male"));
        userInfo.setBirthDate(Optional.of(LocalDate.of(3220, 1, 1)));
        userInfo.setZoneInfo(Optional.of("America/Chicago"));
        userInfo.setLocale(Optional.of("en-US"));
        userInfo.setPhoneNumber(Optional.of("123-456-7891"));

        net.tokensmith.authorization.register.request.Address address = new net.tokensmith.authorization.register.request.Address();
        address.setStreetAddress1("123 Best Jedi Lane");
        address.setStreetAddress2(Optional.of("#1"));
        address.setLocality("Chicago");
        address.setRegion("IL");
        address.setPostalCode("60606");
        address.setCountry("Coruscant");
        userInfo.setAddress(Optional.of(address));

        return userInfo;
    }

    public static String makeRandomEmail() {
        return "test-" + UUID.randomUUID().toString() + "@tokensmith.net";
    }

    public static ResourceOwner makeResourceOwnerWithProfile() throws URISyntaxException {
        ResourceOwner ro = new ResourceOwner();
        ro.setId(UUID.randomUUID());

        ro.setEmail(makeRandomEmail());
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run(PLAIN_TEXT_PASSWORD);
        ro.setPassword(hashedPassword);
        ro.setEmailVerified(false);
        ro.setProfile(makeProfile(ro.getId()));

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

        profile.setGivenNames(givenNames(profile.getId()));
        profile.setFamilyNames(givenNames(profile.getId()));
        return profile;
    }


    public static List<Name> givenNames(UUID profileId) {
        List<Name> names = new ArrayList<>();
        Name name = EntityFactory.givenName(profileId);
        names.add(name);
        return names;
    }

    public static Name givenName(UUID profileId) {
        Name name = new Name();
        name.setId(UUID.randomUUID());
        name.setResourceOwnerProfileId(profileId);
        name.setName("Obi-wan");
        name.setUpdatedAt(OffsetDateTime.now());
        name.setCreatedAt(OffsetDateTime.now());
        return name;
    }

    public static List<Name> familyNames(UUID profileId) {
        List<Name> names = new ArrayList<>();
        Name name = EntityFactory.familyName(profileId);
        names.add(name);
        return names;
    }

    public static Name familyName(UUID profileId) {
        Name name = new Name();
        name.setId(UUID.randomUUID());
        name.setResourceOwnerProfileId(profileId);
        name.setName("Kenobi");
        name.setUpdatedAt(OffsetDateTime.now());
        name.setCreatedAt(OffsetDateTime.now());
        return name;
    }

}

