package integration.authorization.openid.grant.code.request.ValidateOpenIdParams.validation.Scopes;

import helper.ValidateParamsAttributes;
import integration.authorization.openid.grant.code.request.ValidateOpenIdParams.BaseTest;
import org.junit.Test;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.ScopesException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;

import java.util.UUID;


public class ClientFoundRedirectMismatchTest extends BaseTest {

    public static String REDIRECT_URI = "https://rootservices.org/continue";

    public ValidateParamsAttributes makeValidateParamsAttributes(UUID clientId) {
        ValidateParamsAttributes p = new ValidateParamsAttributes();

        p.clientIds.add(clientId.toString());
        p.redirectUris.add(REDIRECT_URI);
        p.responseTypes.add(ResponseType.CODE.toString());

        return p;
    }

    @Test
    public void scopeIsInvalidShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());
        p.scopes.add("invalid-scope");

        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerExceptionNoCause(p, expectedErrorCode);
    }

    @Test
    public void scopesHasTwoItemsShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());

        p.scopes.add("profile");
        p.scopes.add("profile");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

    @Test
    public void scopeIsBlankStringShouldThrowInformResourceOwnerException() throws Exception {
        Client c = loadConfidentialClient();

        ValidateParamsAttributes p = makeValidateParamsAttributes(c.getUuid());

        p.scopes.add("");

        Exception expectedDomainCause = new ScopesException();
        int expectedErrorCode = ErrorCode.REDIRECT_URI_MISMATCH.getCode();

        runExpectInformResourceOwnerException(p, expectedDomainCause, expectedErrorCode);
    }

}
