package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.ResponseType;


import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId, URI redirect) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("redirect_uri").add(redirect.toString());
        p.get("state").add("some-state");

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
        p.put("response_type", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
        p.get("response_type").add("invalid-response-type");

        Exception cause = null;
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getDescription();
        String expectedError = "unsupported_response_type";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());

        p.get("response_type").add("CODE");
        p.get("response_type").add("CODE");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
        p.get("response_type").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId(), c.getRedirectURI());
        p.get("response_type").add("TOKEN");

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionWithStateNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
