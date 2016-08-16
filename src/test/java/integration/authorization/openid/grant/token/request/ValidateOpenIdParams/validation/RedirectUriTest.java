package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.RedirectUriException;
import org.rootservices.authorization.persistence.entity.Client;


public class RedirectUriTest extends BaseTest {

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client c) {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce(c);
        p.redirectUris.clear();

        return p;
    }

    @Test
    public void redirectUrisIsNullShouldThrowInformResourceOwner() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris = null;

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_NULL.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void redirectUriIsEmptyListShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_EMPTY_LIST.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void redirectUrisIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris.add("");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_EMPTY_VALUE.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void redirectUrisHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris.add(c.getRedirectURI().toString());
        p.redirectUris.add(c.getRedirectURI().toString());

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MORE_THAN_ONE_ITEM.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    @Test
    public void redirectUriIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris.add("invalid-uri");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    public void redirectUriIsNotHttpsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris.add("http://rootservices.org");

        Exception expectedDomainCause = new RedirectUriException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_DATA_TYPE.getCode();

        runExpectInformResourceOwnerException(p,expectedDomainCause,expectedErrorCode);
    }

    @Test
    public void redirectUriDoesNotMatchClientShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.redirectUris.add("https://rootservices.org/continue");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }
}
