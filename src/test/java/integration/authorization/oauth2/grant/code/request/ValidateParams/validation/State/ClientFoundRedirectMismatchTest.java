package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("https://rootservices.org/continue");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.states.add("some-state");
        p.states.add("some-state");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.clientIds.add(c.getUuid().toString());
        p.states.add("");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}