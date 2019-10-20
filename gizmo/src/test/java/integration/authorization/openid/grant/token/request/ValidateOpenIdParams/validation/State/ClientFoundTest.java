package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.State;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParamsWithNonce(Client c) {
        Map<String, List<String>> p = super.makeParamsWithNonce(c);
        p.get("state").clear();

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("state").add("some-state");
        p.get("state").add("some-state");

        Exception cause = new OptionalException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.STATE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void stateIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("state").add("");

        Exception cause = new OptionalException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.STATE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }
}