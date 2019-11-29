package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.Scopes;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    private static String REDIRECT_URI = "https://tokensmith.net";

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> p = super.makeParams();

        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("redirect_uri").add(REDIRECT_URI);
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {

        Map<String, List<String>> p = makeParams();
        p.get("scope").add("invalid-scope");

        Exception cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {

        Map<String, List<String>> p = makeParams();
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("scope").add("");

        Exception cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }
}
