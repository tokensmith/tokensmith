package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.Scopes;


import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;


public class ClientFoundTest extends BaseTest {

    public Map<String, List<String>> makeParams(Client c) {
        Map<String, List<String>> p = super.makeParams();
        p.get("client_id").add(c.getId().toString());

        for(ResponseType rt: c.getResponseTypes()) {
            p.get("response_type").add(rt.getName());
        }
        p.get("state").add("some-state");

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.get("scope").add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedDescription = ErrorCode.SCOPES_NOT_SUPPORTED.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionWithStateNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.get("scope").add("profile");
        p.get("scope").add("profile");

        Exception cause = new OptionalException();
        String expectedDescription = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        Map<String, List<String>> p = makeParams(c);
        p.get("scope").add("");

        Exception cause = new OptionalException();
        String expectedDescription = ErrorCode.SCOPES_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionWithState(p, cause, 1, expectedError, expectedDescription, c.getRedirectURI());
    }
}
