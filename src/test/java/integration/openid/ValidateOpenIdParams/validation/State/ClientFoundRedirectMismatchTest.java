package integration.openid.ValidateOpenIdParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.openid.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URISyntaxException;
import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes(UUID uuid) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(uuid.toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        p.redirectUris.add("https://rootservices.org/continue");

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());
        p.states.add("some-state");
        p.states.add("some-state");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() throws URISyntaxException {
        Client c = loadClientWithOpenIdScope.run();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());
        p.states.add("");

        Exception expectedDomainCause = new StateException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}