package helper;

import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.security.TextHasher;
import org.rootservices.authorization.security.TextHasherImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class FixtureFactory {

    /*
    Constructs a default Client
     */
    public static Client makeClient() throws URISyntaxException {
        UUID uuid = UUID.randomUUID();
        ResponseType rt = ResponseType.CODE;
        URI redirectUri = new URI("https://rootservices.org");
        return new Client(uuid, rt, redirectUri);
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
