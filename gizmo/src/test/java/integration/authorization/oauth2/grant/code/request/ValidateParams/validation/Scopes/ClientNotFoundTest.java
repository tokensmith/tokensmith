package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.Scopes;


import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {

        Map<String, List<String>> p = makeParams();
        p.get("scope").add("invalid-scope");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Map<String, List<String>> p = makeParams();
        p.get("scope").add("");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
