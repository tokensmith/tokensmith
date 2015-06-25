package integration.ValiateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Scenario: Response type fails validation And Client is not found.
 *
 * Given client ids has one item that is assigned to a random UUID
 * And there is not a client record in the db for that UUID
 * And response types has one item that is [method]
 * When the params are validated
 * Then raise a InformResourceOwner exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode].
 */
public class ClientNotFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());

        return p;
    }

    @Test
    public void paramIsNull() throws StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes = null;
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void emptyList() throws StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void invalid() throws StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("invalid-response-type");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void duplicate() throws StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void emptyValue() throws StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
