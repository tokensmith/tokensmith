package integration.ValiateParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.StateException;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Scenario: Scopes fails validation And Client is not found.
 *
 * Given client ids has one item that is assigned to a random UUID
 * And response types has one item that is assigned to CODE
 * And scopes is [method]
 * And there is not a client record in the db for that UUID
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
    public void invalid() throws URISyntaxException, StateException {

        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("invalid-scope");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void duplicate() throws URISyntaxException, StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void emptyValue() throws URISyntaxException, StateException {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.scopes.add("");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
