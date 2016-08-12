package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Scopes;

import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Client;

import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public ValidateParamsWithNonce makeValidateParamsWithNonce(Client c) {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce(c);
        p.redirectUris.clear();
        p.redirectUris.add(REDIRECT_URI);
        p.scopes.clear();

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsWithNonce p = makeValidateParamsWithNonce(c);
        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

}
