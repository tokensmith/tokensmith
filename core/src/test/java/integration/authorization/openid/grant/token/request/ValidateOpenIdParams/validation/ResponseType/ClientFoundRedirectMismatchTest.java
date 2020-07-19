package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.ResponseType;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.parser.exception.RequiredException;
import net.tokensmith.repository.entity.Client;
import org.junit.Test;

import java.util.List;
import java.util.Map;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://tokensmith.net/continue";

    public Map<String, List<String>> makeParamsWithNonce(Client c) {
        Map<String, List<String>> p = super.makeParamsWithNonce(c);
        p.get("redirect_uri").clear();
        p.get("redirect_uri").add(REDIRECT_URI);

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.put("response_type", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("invalid-response-type");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").add("TOKEN");
        p.get("response_type").add("TOKEN");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}
