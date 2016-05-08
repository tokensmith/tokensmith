package integration.authorization.oauth2.grant.code.request.ValidateParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        return p;
    }

    @Test
    public void stateHasTwoItemsShouldThrowInformResourceOwnerException() {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.states.add("some-state");
        p.states.add("some-state");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void stateIsBlankStringShouldThrowInformResourceOwnerException() {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.states.add("");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
