package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Nonce;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ResponseTypeException;
import org.rootservices.authorization.persistence.entity.Client;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client c) {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce(c);
        p.nonces.clear();
        p.redirectUris.add(REDIRECT_URI);

        return p;
    }

    @Test
    public void noncesIsNullShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.nonces = null;

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);

    }

    @Test
    public void responseTypeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.responseTypes.clear();
        p.responseTypes.add("invalid-response-type");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.responseTypes.add("TOKEN");
        p.responseTypes.add("TOKEN");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void responseTypeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.responseTypes.clear();
        p.responseTypes.add("");

        Exception expectedDomainCause = new ResponseTypeException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
