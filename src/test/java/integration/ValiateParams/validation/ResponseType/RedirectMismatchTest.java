package integration.ValiateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;

/**
 * Scenario: Response type fails validation And Client is found And redirects don't match.
 *
 * Given a client, c, exists in the db
 * And c's redirect uri is https://rootservices.org
 * And client ids has one item that is assigned to c's UUID
 * And redirect uris has one item that is assigned to https://rootservices.org/continue
 * And response types has one item that is [method]
 * When the params are validated
 * Then raise a InformResourceOwner exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode]
 */
public class RedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    @Test
    public void paramIsNull() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);

        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void emptyList() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);

    }

    @Test
    public void invalid() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void duplicate() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void emptyValue() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
