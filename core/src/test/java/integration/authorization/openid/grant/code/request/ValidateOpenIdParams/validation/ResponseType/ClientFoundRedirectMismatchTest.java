package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.ResponseType;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.parse.exception.RequiredException;
import net.tokensmith.repository.entity.Client;


import java.util.*;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(REDIRECT_URI);

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.put("response_type", null);

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("invalid-response-type");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());

        p.get("response_type").add("CODE");
        p.get("response_type").add("CODE");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("");

        Exception cause = new RequiredException();

        runExpectInformResourceOwnerException(p, cause);
    }
}
