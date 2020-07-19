package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.State;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import org.junit.Test;

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
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() {
        Map<String, List<String>> p = makeParams();
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() {
        Map<String, List<String>> p = makeParams();
        p.get("state").add("");

        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }
}
