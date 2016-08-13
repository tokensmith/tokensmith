package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;


public class ClientNotFoundTest extends BaseTest {

    @Test
    public void noncesIsNullShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.nonces = null;

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.nonces.clear();

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformResourceException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.nonces.clear();
        p.nonces.add("some-nonce");
        p.nonces.add("some-nonce");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.nonces.clear();
        p.nonces.add("");

        RecordNotFoundException expectedDomainCause = new RecordNotFoundException();
        int errorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, errorCode);
    }
}
