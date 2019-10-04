package helpers.fixture;

import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.register.request.*;
import org.rootservices.authorization.security.ciphers.HashTextRandomSalt;
import org.rootservices.authorization.security.ciphers.HashTextRandomSaltImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class EntityFactory {
    public static String SECURE_REDIRECT_URI = "https://rootservices.org";
    public static String ISSUER = "https://sso.rootservices.org";

    public static ConfidentialClient makeConfidentialClient(Client client) {
        ConfidentialClient confidentialClient = new ConfidentialClient();
        confidentialClient.setId(UUID.randomUUID());
        confidentialClient.setClient(client);
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run("password");
        confidentialClient.setPassword(hashedPassword.getBytes());

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
        userInfo.setEmail("obi-wan-kenobi-" + UUID.randomUUID() + "@rootservices.org");
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

        org.rootservices.authorization.register.request.Address address = new org.rootservices.authorization.register.request.Address();
        address.setStreetAddress1("123 Best Jedi Lane");
        address.setStreetAddress2(Optional.of("#1"));
        address.setLocality("Chicago");
        address.setRegion("IL");
        address.setPostalCode("60606");
        address.setCountry("Coruscant");
        userInfo.setAddress(Optional.of(address));

        return userInfo;
    }

}

