package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Client;


public class ClientFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes(Client client) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();

        p.clientIds.add(client.getUuid().toString());
        p.redirectUris.add(client.getRedirectURI().toString());
        p.responseTypes.add(client.getResponseType().toString());

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c);
        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedDescription = ErrorCode.SCOPES_NOT_SUPPORTED.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c);

        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.SCOPES_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c);

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.SCOPES_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.SCOPES_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_scope";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
