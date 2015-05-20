package helper.fixture;

import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.security.TextHasher;
import org.rootservices.authorization.security.TextHasherImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
}
