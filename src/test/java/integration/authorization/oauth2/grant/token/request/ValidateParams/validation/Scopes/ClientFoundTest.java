package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ScopesException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


public class ClientFoundTest extends BaseTest {

    @Test
    public void scopeIsInvalidShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        for(ResponseType responseType: c.getResponseTypes()) {
            p.responseTypes.add(responseType.getName());
        }

        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedDescription = ErrorCode.SCOPES_NOT_SUPPORTED.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        for(ResponseType responseType: c.getResponseTypes()) {
            p.responseTypes.add(responseType.getName());
        }

        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        for(ResponseType responseType: c.getResponseTypes()) {
            p.responseTypes.add(responseType.getName());
        }

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.SCOPES_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
