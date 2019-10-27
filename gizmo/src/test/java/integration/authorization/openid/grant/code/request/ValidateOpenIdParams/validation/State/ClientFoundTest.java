package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.State;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.parse.exception.OptionalException;
import net.tokensmith.authorization.persistence.entity.Client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId, URI redirectUri) {
        Map<String, List<String>> p = super.makeParams();

        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(redirectUri.toString());
        p.get("response_type").add("CODE");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
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
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
        p.get("state").add("");

        Exception cause = new OptionalException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.STATE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }
}
