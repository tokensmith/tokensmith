package integration.authorization.oauth2.grant.token.request.ValidateParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.authorization.oauth2.grant.token.request.ValidateParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add("CODE");

        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add("CODE");

        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadClient();

        ValidateParamsAttributes p = new ValidateParamsAttributes();
        p.clientIds.add(c.getId().toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add("CODE");

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

}
