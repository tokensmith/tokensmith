package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.State;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client c) {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce(c);
        p.redirectUris.clear();
        p.redirectUris.add("https://rootservices.org/continue");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.states.add("some-state");
        p.states.add("some-state");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.states.add("");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}