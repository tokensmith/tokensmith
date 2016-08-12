package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation;

import helper.ValidateParamsAttributes;
import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ClientIdException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class ClientIdTest extends BaseTest {

    public ValidateParamsWithNonce makeValidateParamsWithNonce() {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce();
        p.clientIds.clear();

        return p;
    }

    @Test
    public void clientIdIsNullShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.clientIds = null;

        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_NULL.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();

        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_LIST.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.clientIds.add("invalid");

        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdsHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.clientIds.add(UUID.randomUUID().toString());
        p.clientIds.add(UUID.randomUUID().toString());

        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void clientIdIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.clientIds.add("");

        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
