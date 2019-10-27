package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.Scopes;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.parse.exception.OptionalException;
import net.tokensmith.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();

        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(REDIRECT_URI);
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("scope").add("invalid-scope");

        runExpectInformResourceOwnerExceptionNoCause(p);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new OptionalException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("scope").add("");

        Exception cause = new OptionalException();

        runExpectInformResourceOwnerException(p, cause);
    }

}
