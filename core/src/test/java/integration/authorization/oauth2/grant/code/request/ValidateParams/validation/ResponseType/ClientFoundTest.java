package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.ResponseType;


import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.parser.exception.RequiredException;
import net.tokensmith.repository.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams(UUID clientId) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(clientId.toString());
        p.get("state").add("some-state");

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.put("response_type", null);

        Exception cause = new RequiredException();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());

        Exception cause = new RequiredException();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("invalid-response-type");

        Exception cause = new RequiredException();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getDescription();
        String expectedError = "unsupported_response_type";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("CODE");
        p.get("response_type").add("CODE");

        Exception cause = new RequiredException();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("");

        Exception cause = new RequiredException();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        Map<String, List<String>> p = makeParams(c.getId());
        p.get("response_type").add("TOKEN");

        Integer errorCode =  ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionWithStateNoCause(p, errorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
