package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.State;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.parse.exception.OptionalException;
import net.tokensmith.repository.entity.Client;

import java.util.List;
import java.util.Map;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public Map<String, List<String>> makeValidateParamsWithNonce(Client c) {
        Map<String, List<String>> p = super.makeParamsWithNonce(c);
        p.get("redirect_uri").clear();
        p.get("redirect_uri").add("https://rootservices.org/continue");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeValidateParamsWithNonce(c);
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new OptionalException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeValidateParamsWithNonce(c);
        p.get("state").add("");

        Exception cause = new OptionalException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}