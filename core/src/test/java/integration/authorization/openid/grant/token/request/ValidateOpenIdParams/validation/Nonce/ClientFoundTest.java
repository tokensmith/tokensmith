package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;


import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.parse.exception.RequiredException;
import net.tokensmith.repository.entity.Client;

import java.util.List;
import java.util.Map;


public class ClientFoundTest extends BaseTest {

    @Test
    public void noncesIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.put("nonce", null);

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.NONCE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("nonce").clear();

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.NONCE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("nonce").clear();
        p.get("nonce").add("some-nonce");
        p.get("nonce").add("some-nonce");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.NONCE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        Map<String, List<String>> p = makeParamsWithNonce(c);
        p.get("nonce").clear();
        p.get("nonce").add("");

        Exception cause = new RequiredException();
        int expectedErrorCode = 1;
        String expectedDescription = ErrorCode.NONCE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
