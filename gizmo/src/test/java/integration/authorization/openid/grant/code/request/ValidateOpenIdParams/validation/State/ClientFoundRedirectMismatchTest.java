package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.State;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    private static String REDIRECT_URI = "https://rootservices.org/continue";

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();

        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(REDIRECT_URI);
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new OptionalException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("");

        Exception cause = new OptionalException();

        runExpectInformResourceOwnerException(p, cause);
    }
}