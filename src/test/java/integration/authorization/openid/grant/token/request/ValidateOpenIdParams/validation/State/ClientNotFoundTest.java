package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.State;

import helper.ValidateParamsAttributes;
import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public ValidateParamsWithNonce makeValidateParamsWithNonce() {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce();
        p.states.clear();

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.states.add("some-state");
        p.states.add("some-state");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.states.add("");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
