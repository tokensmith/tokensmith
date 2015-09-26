package integration.ValiateParams.validation;

import helper.ValidateParamsAttributes;
import integration.ValiateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.protocol.authorization.builder.exception.ClientIdException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.StateException;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 * Created by tommackenzie on 3/1/15.
 */
public class ClientIdTest extends BaseTest {

    @Test
    public void paramIsNull() throws InformClientException, StateException {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds = null;
        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_NULL.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void emptyList() throws InformClientException, StateException {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_LIST.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void invalid() throws InformClientException, StateException {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add("invalid");
        int errorCode = ErrorCode.CLIENT_ID_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void duplicate() throws InformClientException, StateException {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add(UUID.randomUUID().toString());
        p.clientIds.add(UUID.randomUUID().toString());
        int errorCode = ErrorCode.CLIENT_ID_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void emptyValue() throws InformClientException, StateException {
        ValidateParamsAttributes p = new ValidateParamsAttributes();
        ClientIdException expectedDomainCause = new ClientIdException();
        p.clientIds.add("");
        int errorCode = ErrorCode.CLIENT_ID_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
