package integration.authorization.openid.grant.token.request.ValidateOpenIdParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import helper.ValidateParamsWithNonce;
import integration.authorization.openid.grant.token.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public class ClientNotFoundTest extends BaseTest {

    private static String REDIRECT_URI = "https://rootservices.org";

    public ValidateParamsWithNonce makeValidateParamsWithNonce() {
        ValidateParamsWithNonce p = super.makeValidateParamsWithNonce();
        p.scopes.clear();

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {

        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.scopes.add("invalid-scope");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        ValidateParamsWithNonce p = makeValidateParamsWithNonce();
        p.scopes.add("");

        Exception expectedDomainCause = new RecordNotFoundException();
        int expectedErrorCode = ErrorCode.CLIENT_NOT_FOUND.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }
}
