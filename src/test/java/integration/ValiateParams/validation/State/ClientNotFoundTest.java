package integration.ValiateParams.validation.State;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Scenario: State fails validation And Client is not found.
 *
 * Given client ids has one item that is assigned to a random UUID
 * And there is not a client record in the db for that UUID
 * And response types has one item assigned to CODE
 * And states has one item that is [method]
 * When the params are validated
 * Then raise a InformResourceOwner exception, e
 * And expect e's cause to be [expectedDomainCause]
 * And expects e's error code to be [errorCode].
 */
public class ClientNotFoundTest extends BaseTest {

    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());
        p.responseTypes.add(ResponseType.CODE.toString());

        return p;
    }

    @Test
    public void duplicate() {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.states.add("some-state");
        p.states.add("some-state");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void emptyValue() {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.states.add("");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
