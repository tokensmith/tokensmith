package integration.code.ValidateParams.validation.ResponseType;

import helper.ValidateParamsAttributes;
import integration.code.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Feature: response type fails validation and client is not found.
 *
 * Given a instance of ValidateParams, subject
 *
 */
public class ClientNotFoundTest extends BaseTest {


    public ValidateParamsAttributes makeValidateParamsAttributes() {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(UUID.randomUUID().toString());

        return p;
    }

    /**
     * Scenario 1: response type is null
     *
     * When subject.run is executed
     * And clientIds has one item that is a random UUID
     * And responseTypes is null
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * And a client is not found in the db
     * Then raise a InformResourceOwner exception, e
     * And expects e's error code to be CLIENT_NOT_FOUND
     * And expect e's cause to be RecordNotFoundException
     */
    @Test
    public void responseTypeIsNullShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes = null;
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    /**
     * Scenario 2: response type is empty list
     *
     * When subject.run is executed
     * And clientIds has one item that is a random UUID
     * And responseTypes is a empty list
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * And a client is not found in the db
     * Then raise a InformResourceOwner exception, e
     * And expects e's error code to be CLIENT_NOT_FOUND
     * And expect e's cause to be RecordNotFoundException
     */
    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    /**
     * Scenario 3: response type is invalid
     *
     * When subject.run is executed
     * And clientIds has one item that is a random UUID
     * And responseTypes has one item, "invalid-response-type"
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * And a client is not found in the db
     * Then raise a InformResourceOwner exception, e
     * And expects e's error code to be CLIENT_NOT_FOUND
     * And expect e's cause to be RecordNotFoundException
     */
    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("invalid-response-type");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    /**
     * Scenario 4: response type has two items
     *
     * When subject.run is executed
     * And clientIds has one item that is a random UUID
     * And responseTypes has two items, [CODE, CODE]
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * And a client is not found in the db
     * Then raise a InformResourceOwner exception, e
     * And expects e's error code to be CLIENT_NOT_FOUND
     * And expect e's cause to be RecordNotFoundException
     */
    @Test
    public void responseTypeHasTwoItemsShouldThrowInformClientException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add(ResponseType.CODE.toString());
        p.responseTypes.add(ResponseType.CODE.toString());
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    /**
     * Scenario 5: response type has one item, ""
     *
     * When subject.run is executed
     * And clientIds has one item that is a random UUID
     * And responseTypes has one item, ""
     * And redirectUris is a empty list
     * And scopes is a empty list
     * And states is a empty list
     * And a client is not found in the db
     * Then raise a InformResourceOwner exception, e
     * And expects e's error code to be CLIENT_NOT_FOUND
     * And expect e's cause to be RecordNotFoundException
     */
    @Test
    public void responseTypeIsBlankStringShouldThrowInformClientException() throws Exception {
        ValidateParamsAttributes p = makeValidateParamsAttributes();
        p.responseTypes.add("");
        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
