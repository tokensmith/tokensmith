package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;


public class ClientFoundTest extends BaseTest {

    @Test
    public void responseTypeIsNullShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");
        p.responseTypes = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_NULL.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_NULL.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_LIST.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_DATA_TYPE.getDescription();
        String expectedError = "unsupported_response_type";

        runExpectInformClientExceptionWithState(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");

        p.responseTypes.add("CODE");
        p.responseTypes.add("CODE");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientExceptionWithState(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void responseTypesDontMatchShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.states.add("some-state");
        p.responseTypes.add("TOKEN");

        int expectedErrorCode = ErrorCode.RESPONSE_TYPE_MISMATCH.getCode();
        String expectedDescription = ErrorCode.RESPONSE_TYPE_MISMATCH.getDescription();
        String expectedError = "unauthorized_client";

        runExpectInformClientExceptionWithStateNoCause(p, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }
}
