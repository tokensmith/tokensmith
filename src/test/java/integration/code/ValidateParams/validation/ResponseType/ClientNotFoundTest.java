package integration.code.ValidateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.code.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());

        return p;
    }

    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes = null;
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("invalid-response-type");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
