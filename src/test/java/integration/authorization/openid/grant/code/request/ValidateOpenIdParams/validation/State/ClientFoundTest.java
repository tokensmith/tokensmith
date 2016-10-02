package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;


public class ClientFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes(Client c) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(c.getRedirectURI().toString());
        p.responseTypes.add("CODE");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c);
        p.states.add("some-state");
        p.states.add("some-state");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.STATE_MORE_THAN_ONE_ITEM.getCode();
        String expectedDescription = ErrorCode.STATE_MORE_THAN_ONE_ITEM.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());
    }

    @Test
    public void stateIsBlankStringShouldThrowInformClientException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c);
        p.states.add("");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.STATE_EMPTY_VALUE.getCode();
        String expectedDescription = ErrorCode.STATE_EMPTY_VALUE.getDescription();
        String expectedError = "invalid_request";

        runExpectInformClientException(p, expectedDomainCause, expectedErrorCode, expectedError, expectedDescription, c.getRedirectURI());

    }
}
