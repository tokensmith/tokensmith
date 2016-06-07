package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;

import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


public class ClientFoundTest extends BaseTest {

    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_NULL.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getDescription();
        String expectedError = "unsupported_response_type";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());

        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws URISyntaxException, StateException {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
