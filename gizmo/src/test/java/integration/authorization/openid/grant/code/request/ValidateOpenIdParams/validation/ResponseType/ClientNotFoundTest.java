package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.ResponseType;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {
    private static String REDIRECT_URI = "https://rootservices.org";

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("redirect_uri").add(REDIRECT_URI);

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.put("response_type", null);

        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("invalid-response-type");
        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("CODE");
        p.get("response_type").add("CODE");
        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("response_type").add("");
        RecordNotFoundException cause = new RecordNotFoundException();

        runExpectInformResourceOwnerException(p, cause);
    }
}
