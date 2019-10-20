package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.ResponseType;

import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.List;
import java.util.Map;


public class ClientFoundTest extends BaseTest {

    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.put("response_type", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("invalid-response-type");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getDescription();
        String expectedError = "unsupported_response_type";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("TOKEN");
        p.get("response_type").add("TOKEN");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("response_type").clear();
        p.get("response_type").add("CODE");

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionWithStateNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
