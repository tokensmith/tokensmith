package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.Scopes;


import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams() {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(UUID.randomUUID().toString());
        p.get("response_type").add("TOKEN");

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {

        Map<String, List<String>> p = makeParams();
        p.get("scope").add("invalid-scope");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Map<String, List<String>> p = makeParams();
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws URISyntaxException, StateException {
        Map<String, List<String>> p = makeParams();
        p.get("scope").add("");

        Exception cause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, cause, expectedErrorCode);
    }
}
