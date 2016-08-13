package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.exception.NonceException;
import org.rootservices.authorization.persistence.entity.Client;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client c) {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce(c);
        p.redirectUris.clear();
        p.redirectUris.add(REDIRECT_URI);
        p.nonces.clear();

        return p;
    }

    @Test
    public void noncesIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces = null;

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void noncesIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);

    }

    @Test
    public void noncesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces.add("some-nonce");
        p.nonces.add("some-nonce");

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void noncesIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces.add("");

        Exception expectedDomainCause = new NonceException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
