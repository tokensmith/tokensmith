package integration.authorization.oauth2.grant.code.request.ValidateParams.validation;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.code.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ClientIdException;

import java.util.UUID;


/**
 * Created by tommackenzie on 3/1/15.
 */
public class ClientIdTest extends BaseTest {

    @Test
    public void clientIdIsNullShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds = null;
        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_NULL.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_LIST.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add("invalid");
        int errorCode = ErrorCode.CLIENT_ID_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdsHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add(UUID.randomUUID().toString());
        p.clientIds.add(UUID.randomUUID().toString());
        int errorCode = ErrorCode.CLIENT_ID_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add("");
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
