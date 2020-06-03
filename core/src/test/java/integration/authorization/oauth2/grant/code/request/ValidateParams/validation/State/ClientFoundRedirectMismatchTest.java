package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.State;

import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.parser.exception.OptionalException;
import net.tokensmith.repository.entity.Client;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("response_type").add("CODE");
        p.get("redirect_uri").add("https://tokensmith.net/continue");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new OptionalException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("state").add("");

        Exception cause = new OptionalException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}