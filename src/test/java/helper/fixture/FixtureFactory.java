package helper.fixture;

import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.security.TextHasher;
import org.rootservices.authorization.security.TextHasherImpl;

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

    public static Client makeClientWithScopes() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectUri = new URI("https://rootservices.org");

        Client client = new Client(uuid, rt, redirectUri);
        List<Scope> scopes = makeScopes();
        client.setScopes(scopes);
        return client;
    }

    public static ConfidentialClient makeConfidentialClient(Client client) {
        ConfidentialClient confidentialClient = new ConfidentialClient();
        confidentialClient.setUuid(UUID.randomUUID());
        confidentialClient.setClient(client);
        TextHasher textHasher = new TextHasherImpl();
        String hashedPassword = textHasher.run("password");
        confidentialClient.setPassword(hashedPassword.getBytes());

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
        ro.setEmail("test@rootservices.org");
        TextHasher textHasher = new TextHasherImpl();
        String hashedPassword = textHasher.run("password");
        ro.setPassword(hashedPassword.getBytes());

        return ro;
    }

    public static AuthCode makeAuthCode(UUID resourceOwnerUUID, UUID clientUUID) {
        AuthCode authCode = new AuthCode();
        authCode.setUuid(UUID.randomUUID());
        authCode.setCode("authortization_code".getBytes());
        authCode.setResourceOwnerUUID(resourceOwnerUUID);
        authCode.setClientUUID(clientUUID);
        authCode.setExpiresAt(OffsetDateTime.now().plusMinutes(1));

        return authCode;
    }

    public static AccessRequest makeAccessRequest(UUID authCodeUUID) throws URISyntaxException {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setUuid(UUID.randomUUID());
        accessRequest.setRedirectURI(Optional.of(new URI("https://rootservices.org")));
        accessRequest.setAuthCodeUUID(authCodeUUID);

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
