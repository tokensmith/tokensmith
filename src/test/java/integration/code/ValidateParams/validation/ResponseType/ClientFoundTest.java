package integration.code.ValidateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.code.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


/**
 * Feature: response type fails validation and client is found.
 *
 * Given a instance of ValidateParams, subject
 * And a public client, c
 * And c's response_type is, CODE
 * And c's id is a randomly generated UUID
 * And c's redirect uri is, https://rootservices.org
 * And c's scopes are, profile
 * And c is inserted into the database
 */
public class ClientFoundTest extends BaseTest {

    /**
     * Scenario 1: response type is null
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes is null
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_NULL
     * And expect e's error message to be "invalid_request"
     * And expect e's cause to be ResponseTypeException
     * And expects e's redirect uri to be c's redirect uri
     */
    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_NULL.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Scenario 2: response type is a empty list
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes is a empty list
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_EMPTY_LIST
     * And expect e's error message to be "invalid_request"
     * And expects e's redirect uri to be c's redirect uri
     * And expect e's cause to be ResponseTypeException
     */
    @Test
    public void emptyListShouldThrowInformClientException() throws Exception {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());

    }

    /**
     * Scenario 3: response type is invalid
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has one item, "invalid-response-type"
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_DATA_TYPE
     * And expect e's error message to be "unsupported_response_type"
     * And expects e's redirect uri to be c's redirect uri
     * And expect e's cause to be ResponseTypeException
     */
    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode();
        String expectedError = "unsupported_response_type";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Scenario 4: response type has two items
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has two items, [CODE, CODE]
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_MORE_THAN_ONE_ITEM
     * And expect e's error message to be "invalid_request"
     * And expects e's redirect uri to be c's redirect uri
     * And expect e's cause to be ResponseTypeException
     */
    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Scenario 5: response type has one item, ""
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has one item, ""
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_EMPTY_VALUE
     * And expect e's error message to be "invalid_request"
     * And expects e's redirect uri to be c's redirect uri
     * And expect e's cause to be ResponseTypeException
     */
    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, c.getRedirectURI());
    }

    /**
     * Scenario 6: response type does not match client's response type
     *
     * When subject.run is executed
     * And clientIds has one item that is c's id.
     * And responseTypes has one item, TOKEN
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * Then raise a InformClientException exception, e
     * And expects e's error code to be RESPONSE_TYPE_MISMATCH
     * And expect e's error message to be "unauthorized_client"
     * And expects e's redirect uri to be c's redirect uri
     * And expect e's cause to be null
     */
    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClientWithScopes.run();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.TOKEN.toString());

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, c.getRedirectURI());
    }
}
