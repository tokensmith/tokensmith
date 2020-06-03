package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.ResponseType;


import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.parser.exception.RequiredException;
import net.tokensmith.repository.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://tokensmith.net/continue";

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(REDIRECT_URI);

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.put("response_type", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c.getId());

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("invalid-response-type");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("TOKEN");
        p.get("response_type").add("TOKEN");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}
