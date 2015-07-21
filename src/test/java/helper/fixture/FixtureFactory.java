package helper.fixture;

import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.HashTextRandomSalt;
import org.rootservices.authorization.security.HashTextRandomSaltImpl;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.rootservices.authorization.security.HashTextStaticSaltImpl;

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
    public static String PLAIN_TEXT_AUTHORIZATION_CODE = "authortization_code";

    public static Client makeClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectUri = new URI(SECURE_REDIRECT_URI);

        Client client = new Client(uuid, rt, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
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
        Scope scope = new Scope();
        scope.setUuid(UUID.randomUUID());
        scope.setName("profile");
        scopes.add(scope);
        return scopes;
    }

    public static ResourceOwner makeResourceOwner() {
        ResourceOwner ro = new ResourceOwner();
        ro.setUuid(UUID.randomUUID());

        ro.setEmail("test-" + UUID.randomUUID().toString() + "@rootservices.org");
        HashTextRandomSalt textHasher = new HashTextRandomSaltImpl();
        String hashedPassword = textHasher.run(PLAIN_TEXT_PASSWORD);
        ro.setPassword(hashedPassword.getBytes());

        return ro;
    }

    public static AuthCode makeAuthCode(AccessRequest accessRequest, boolean isRevoked, String plainTextAuthCode) {
        AuthCode authCode = new AuthCode();
        authCode.setUuid(UUID.randomUUID());
        HashTextStaticSalt textHasher = new HashTextStaticSaltImpl();
        String hashedCode = textHasher.run(plainTextAuthCode);
        authCode.setCode(hashedCode.getBytes());
        authCode.setRevoked(isRevoked);
        authCode.setAccessRequest(accessRequest);
        authCode.setExpiresAt(OffsetDateTime.now().plusMinutes(3));

        return authCode;
    }

    public static AccessRequest makeAccessRequest(UUID resourceOwnerUUID, UUID clientUUID) throws URISyntaxException {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setUuid(UUID.randomUUID());
        accessRequest.setResourceOwnerUUID(resourceOwnerUUID);
        accessRequest.setClientUUID(clientUUID);
        accessRequest.setRedirectURI(Optional.of(new URI(SECURE_REDIRECT_URI)));

        return accessRequest;
    }

    public static Token makeToken(UUID authCodeUUID) {
        Token token = new Token();
        token.setUuid(UUID.randomUUID());
        token.setAuthCodeUUID(authCodeUUID);
        token.setToken("token".getBytes());
        token.setExpiresAt(OffsetDateTime.now());

        return token;
    }
}
