package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.State;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;


public class ClientNotFoundTest extends BaseTest {

    public Map<String, List<String>> makeParamsWithNonce() {
        Map<String, List<String>> p = super.makeParamsWithNonce();
        p.get("state").clear();

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("state").add("");

        RecordNotFoundException cause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, errorCode);
    }
}
