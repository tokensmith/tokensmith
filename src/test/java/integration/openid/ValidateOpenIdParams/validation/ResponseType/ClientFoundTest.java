package integration.openid.ValidateOpenIdParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.openid.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;

/**
 * Scenario: Response type fails validation And Client is found.
 *
 * Given a client, c, exists in the db
 * And client ids has one item that is assigned to c's UUID
 * And the redirect uris has one item that is assigned to c's redirect uri
 * And response types is [method]
 * When the params are validated
 * Then raise a InformClientException exception, e
 * And expects e's error code to be [errorCode]
 * And expects e's redirect uri to be c's redirect uri
 */
public class ClientFoundTest extends BaseTest {

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void paramIsNull() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_NULL.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void emptyList() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());

    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void invalid() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode();
        String expectedError = "unsupported_response_type";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void duplicate() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());

        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Then expect e's cause to be [expectedDomainCause]
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void emptyValue() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Then expect e's cause to be null
     *
     * @throws URISyntaxException
     * @throws StateException
     */
    @Test
    public void mismatch() throws URISyntaxException, StateException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes.add(ResponseType.TOKEN.toString());

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, c.getRedirectURI());
    }
}
