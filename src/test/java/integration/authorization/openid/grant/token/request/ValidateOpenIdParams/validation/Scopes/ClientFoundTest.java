package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Scopes;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParamsWithNonce(Client client) {
        Map<String, List<String>> p = super.makeParamsWithNonce(client);
        p.get("scope").clear();

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("scope").add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedDescription = ErrorCode.SCOPES_NOT_SUPPORTED.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionWithStateNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new OptionalException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("scope").add("");

        Exception cause = new OptionalException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.SCOPES_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
