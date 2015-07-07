package integration.ValiateParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.factory.exception.ScopesException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;

import java.net.URISyntaxException;

/**
 * Scenario: Scopes fails validation And Client is found.
 *
 * Given a client, c, exists in the db
 * And client ids has one item that is assigned to c's UUID
 * And response types has one item that is assigned CODE
 * And scopes is [method]
 * When the params are validated
 * Then raise a InformClientException exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode]
 * And expects e's redirect uri to be c's redirect uri
 */
public class ClientFoundTest extends BaseTest {

    @Test
    public void invalid() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(c.getResponseType().toString());

        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.SCOPES_NOT_SUPPORTED.getCode();
        String expectedError = "invalid_scope";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    @Test
    public void duplicate() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

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
    public void emptyValue() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

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
