package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Scopes;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;


public class ClientNotFoundTest extends BaseTest {

    public Map<String, List<String>> makeParamsWithNonce() {
        Map<String, List<String>> p = super.makeParamsWithNonce();
        p.get("scope").clear();

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {

        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("scope").add("invalid-scope");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParamsWithNonce();
        p.get("scope").add("");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}
