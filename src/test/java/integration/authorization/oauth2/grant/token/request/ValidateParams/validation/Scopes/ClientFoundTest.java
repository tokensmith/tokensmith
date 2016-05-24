package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.ScopesException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;


public class ClientFoundTest extends BaseTest {

    @Test
    public void scopeIsInvalidShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_EMPTY_VALUE.getCode();
        String expectedError = "invalid_scope";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }
}
